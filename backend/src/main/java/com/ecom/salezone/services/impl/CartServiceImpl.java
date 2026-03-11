package com.ecom.salezone.services.impl;

import com.ecom.salezone.dtos.AddItemToCartRequest;
import com.ecom.salezone.dtos.CartDto;
import com.ecom.salezone.enities.Cart;
import com.ecom.salezone.enities.CartItem;
import com.ecom.salezone.enities.Product;
import com.ecom.salezone.enities.User;
import com.ecom.salezone.exceptions.BadApiRequestException;
import com.ecom.salezone.exceptions.ResourceNotFoundException;
import com.ecom.salezone.repository.CartItemRepository;
import com.ecom.salezone.repository.CartRepository;
import com.ecom.salezone.repository.ProductRepository;
import com.ecom.salezone.repository.UserRepository;
import com.ecom.salezone.services.CartService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private static final Logger log =
            LoggerFactory.getLogger(CartServiceImpl.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private CartItemRepository cartItemRepository;

    // ================= ADD ITEM TO CART =================
    @CacheEvict(
            value = "user_cart",
            key = "#userId",
            condition = "@cacheFlags.cartCacheEnabled()"
    )
    @Override
    public CartDto addItemToCart(String userId, AddItemToCartRequest request, String logkey) {

        log.info("LogKey: {} - Entry into addItemToCart method | userId={} payload={}",
                logkey, userId, request);

        int quantity = request.getQuantity();
        String productId = request.getProductId();

        if (quantity <= 0) {
            log.error("LogKey: {} - Invalid quantity requested | userId={} productId={} quantity={}",
                    logkey, userId, productId, quantity);
            throw new BadApiRequestException("Requested quantity is not valid !!");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("LogKey: {} - Product not found | productId={}",
                            logkey, productId);
                    return new ResourceNotFoundException("Product not found in database !!");
                });

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("LogKey: {} - User not found | userId={}",
                            logkey, userId);
                    return new ResourceNotFoundException("user not found in database!!");
                });

        Cart cart = null;
        try {
            cart = cartRepository.findByUser(user).get();
            log.info("LogKey: {} - Existing cart found | userId={}",
                    logkey, userId);
        } catch (NoSuchElementException e) {
            log.info("LogKey: {} - No cart found, creating new cart | userId={}",
                    logkey, userId);
            cart = new Cart();
            cart.setCartId(UUID.randomUUID().toString());
        }

        AtomicReference<Boolean> updated = new AtomicReference<>(false);

        List<CartItem> items = cart.getItems();
        items = items.stream().map(item -> {

            if (item.getProduct().getProductId().equals(productId)) {
                log.info("LogKey: {} - Product already exists in cart. Updating quantity",
                        logkey);

                item.setQuantity(quantity);
                item.setTotalPrice(quantity * product.getDiscountedPrice());
                updated.set(true);
            }
            return item;
        }).collect(Collectors.toList());

        if (!updated.get()) {
            log.info("LogKey: {} - Product not present in cart. Adding new item | productId={}",
                    logkey, productId);

            CartItem cartItem = CartItem.builder()
                    .quantity(quantity)
                    .totalPrice(quantity * product.getDiscountedPrice())
                    .cart(cart)
                    .product(product)
                    .build();

            cart.getItems().add(cartItem);
        }

        cart.setUser(user);

        Cart updatedCart = cartRepository.save(cart);

        log.info("LogKey: {} - Cart updated successfully | userId={}",
                logkey, userId);

        return mapper.map(updatedCart, CartDto.class);
    }

    // ================= UPDATE CART ITEM QUANTITY =================
    @CacheEvict(
            value = "user_cart",
            key = "#userId",
            condition = "@cacheFlags.cartCacheEnabled()"
    )
    @Override
    public CartDto updateCartItemQuantity(String userId, int itemId, int quantity, String logkey) {

        log.info("LogKey: {} - Entry into updateCartItemQuantity | userId={} itemId={} quantity={}",
                logkey, userId, itemId, quantity);

        if (quantity <= 0) {
            log.error("LogKey: {} - Invalid quantity requested | itemId={} quantity={}",
                    logkey, itemId, quantity);
            throw new BadApiRequestException("Quantity must be greater than zero !!");
        }

        // Validate user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("LogKey: {} - User not found | userId={}",
                            logkey, userId);
                    return new ResourceNotFoundException("User not found !!");
                });

        // Get cart of user
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> {
                    log.error("LogKey: {} - Cart not found | userId={}",
                            logkey, userId);
                    return new ResourceNotFoundException("Cart not found !!");
                });

        // Get cart item
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.error("LogKey: {} - Cart item not found | itemId={}",
                            logkey, itemId);
                    return new ResourceNotFoundException("Cart item not found !!");
                });

        // Safety check (VERY IMPORTANT)
        if (!cartItem.getCart().getCartId().equals(cart.getCartId())) {
            log.error("LogKey: {} - Cart item does not belong to user cart | userId={} itemId={}",
                    logkey, userId, itemId);
            throw new BadApiRequestException("Cart item does not belong to this user !!");
        }

        // Stock validation
        Product product = cartItem.getProduct();

        if (quantity > product.getQuantity()) {
            log.error("LogKey: {} - Requested quantity exceeds stock | productId={} stock={} requested={}",
                    logkey, product.getProductId(), product.getQuantity(), quantity);
            throw new BadApiRequestException("Requested quantity exceeds available stock !!");
        }

        // Update values
        cartItem.setQuantity(quantity);
        cartItem.setTotalPrice(quantity * product.getDiscountedPrice());

        cartItemRepository.save(cartItem);

        log.info("LogKey: {} - Cart item updated successfully | itemId={}",
                logkey, itemId);

        return mapper.map(cart, CartDto.class);
    }

    // ================= REMOVE ITEM FROM CART =================
    @CacheEvict(
            value = "user_cart",
            key = "#userId",
            condition = "@cacheFlags.cartCacheEnabled()"
    )
    @Override
    public void removeItemFromCart(String userId, int cartItem, String logkey) {

        log.warn("LogKey: {} - Entry into removeItemFromCart method | userId={} cartItemId={}",
                logkey, userId, cartItem);

        CartItem cartItem1 = cartItemRepository.findById(cartItem)
                .orElseThrow(() -> {
                    log.error("LogKey: {} - Cart item not found | cartItemId={}",
                            logkey, cartItem);
                    return new ResourceNotFoundException("Cart Item not found !!");
                });

        cartItemRepository.delete(cartItem1);

        log.info("LogKey: {} - Cart item removed successfully | cartItemId={}",
                logkey, cartItem);
    }

    // ================= CLEAR CART =================
    @CacheEvict(
            value = "user_cart",
            key = "#userId",
            condition = "@cacheFlags.cartCacheEnabled()"
    )
    @Override
    public void clearCart(String userId, String logkey) {

        log.warn("LogKey: {} - Entry into clearCart method | userId={}",
                logkey, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("LogKey: {} - User not found . userId={}",
                            logkey, userId);
                    return new ResourceNotFoundException("user not found in database!!");
                });

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> {
                    log.error("LogKey: {} - Cart not found || userId={}",
                            logkey, userId);
                    return new ResourceNotFoundException("Cart of given user not found !!");
                });

        cart.getItems().clear();
        cartRepository.save(cart);

        log.info("LogKey: {} - Cart cleared successfully | userId={}",
                logkey, userId);
    }

    // ================= GET CART BY USER =================
    @Cacheable(
            value = "user_cart",
            key = "#userId",
            condition = "@cacheFlags.cartCacheEnabled()"
    )
    @Override
    public CartDto getCartByUser(String userId, String logkey) {

        log.info("LogKey: {} - Entry into getCartByUser method | userId={}",
                logkey, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("LogKey: {} - User not found. | userId={}",
                            logkey, userId);
                    return new ResourceNotFoundException("user not found in database!!");
                });

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> {
                    log.error("LogKey: {} - Cart not found. | userId={}",
                            logkey, userId);
                    return new ResourceNotFoundException("Cart of given user not found !!");
                });

        log.info("LogKey: {} - Cart fetched successfully | userId={}",
                logkey, userId);

        return mapper.map(cart, CartDto.class);
    }
}
