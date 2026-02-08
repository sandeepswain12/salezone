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

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    // Logger for debugging & production monitoring
    private static final Logger log = LoggerFactory.getLogger(CartServiceImpl.class);

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

    /**
     * Adds an item to user's cart.
     * - If cart exists → update item
     * - If item exists → update quantity
     * - Else → add new item
     */
    @Override
    public CartDto addItemToCart(String userId, AddItemToCartRequest request, String logkey) {

        log.info("{} Add item to cart called for userId={}, request={}",logkey, userId, request);

        int quantity = request.getQuantity();
        String productId = request.getProductId();

        // Validate requested quantity
        if (quantity <= 0) {
            log.error("{} Invalid quantity {} requested for productId={}",logkey, quantity, productId);
            throw new BadApiRequestException("Requested quantity is not valid !!");
        }

        // Fetch product from DB
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("{} Product not found with id={}",logkey, productId);
                    return new ResourceNotFoundException("Product not found in database !!");
                });

        // Fetch user from DB
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("{} User not found with id={}",logkey, userId);
                    return new ResourceNotFoundException("user not found in database!!");
                });

        // Fetch cart for user or create new one
        Cart cart = null;
        try {
            cart = cartRepository.findByUser(user).get();
            log.info("{} Existing cart found for userId={}",logkey, userId);
        } catch (NoSuchElementException e) {
            log.info("{} No cart found for userId={}, creating new cart",logkey, userId);
            cart = new Cart();
            cart.setCartId(UUID.randomUUID().toString());
        }

        // Flag to check whether item already exists in cart
        AtomicReference<Boolean> updated = new AtomicReference<>(false);

        // Iterate through cart items and update if product already exists
        List<CartItem> items = cart.getItems();
        items = items.stream().map(item -> {

            if (item.getProduct().getProductId().equals(productId)) {
                log.info("{} Product already exists in cart. Updating quantity and price",logkey);

                // Update quantity & total price
                item.setQuantity(quantity);
                item.setTotalPrice(quantity * product.getDiscountedPrice());

                // Mark item as updated
                updated.set(true);
            }
            return item;
        }).collect(Collectors.toList());

        // If product not present in cart, create new CartItem
        if (!updated.get()) {
            log.info("{} Product not present in cart. Adding new item",logkey);

            CartItem cartItem = CartItem.builder()
                    .quantity(quantity)
                    .totalPrice(quantity * product.getDiscountedPrice())
                    .cart(cart)
                    .product(product)
                    .build();

            cart.getItems().add(cartItem);
        }

        // Associate cart with user
        cart.setUser(user);

        // Save cart (JPA will handle cascading)
        Cart updatedCart = cartRepository.save(cart);

        log.info("{} Cart updated successfully for userId={}",logkey, userId);

        // Convert entity to DTO and return
        return mapper.map(updatedCart, CartDto.class);
    }

    /**
     * Removes a single item from cart
     */
    @Override
    public void removeItemFromCart(String userId, int cartItem, String logkey) {

        log.info("{} Remove item from cart called. userId={}, cartItemId={}",logkey, userId, cartItem);

        // Fetch cart item
        CartItem cartItem1 = cartItemRepository.findById(cartItem)
                .orElseThrow(() -> {
                    log.error("{} Cart item not found with id={}",logkey, cartItem);
                    return new ResourceNotFoundException("Cart Item not found !!");
                });

        // Delete cart item
        cartItemRepository.delete(cartItem1);

        log.info("{} Cart item removed successfully. cartItemId={}",logkey, cartItem);
    }

    /**
     * Clears all items from user's cart
     */
    @Override
    public void clearCart(String userId, String logkey) {

        log.info("{} Clear cart called for userId={}",logkey, userId);

        // Fetch user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("{} User not found with id={}",logkey, userId);
                    return new ResourceNotFoundException("user not found in database!!");
                });

        // Fetch cart
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> {
                    log.error("{} Cart not found for userId={}",logkey, userId);
                    return new ResourceNotFoundException("Cart of given user not found !!");
                });

        // Remove all cart items
        cart.getItems().clear();
        cartRepository.save(cart);

        log.info("{} Cart cleared successfully for userId={}",logkey, userId);
    }

    /**
     * Fetches cart details of a user
     */
    @Override
    public CartDto getCartByUser(String userId, String logkey) {

        log.info("{} Get cart by user called. userId={}",logkey, userId);

        // Fetch user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("{} User not found with id={}",logkey, userId);
                    return new ResourceNotFoundException("user not found in database!!");
                });

        // Fetch cart
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> {
                    log.error("{} Cart not found for userId={}",logkey, userId);
                    return new ResourceNotFoundException("Cart of given user not found !!");
                });

        log.info("{} Cart fetched successfully for userId={}",logkey, userId);

        return mapper.map(cart, CartDto.class);
    }
}
