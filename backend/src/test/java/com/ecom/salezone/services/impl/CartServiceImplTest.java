package com.ecom.salezone.services.impl;

import com.ecom.salezone.dtos.AddItemToCartRequest;
import com.ecom.salezone.enities.Cart;
import com.ecom.salezone.enities.CartItem;
import com.ecom.salezone.enities.Product;
import com.ecom.salezone.enities.User;
import com.ecom.salezone.exceptions.BadApiRequestException;
import com.ecom.salezone.repository.CartItemRepository;
import com.ecom.salezone.repository.CartRepository;
import com.ecom.salezone.repository.ProductRepository;
import com.ecom.salezone.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ModelMapper mapper;

    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    @Test
    void addItemToCart_shouldThrowWhenQuantityIsInvalid() {
        AddItemToCartRequest request = new AddItemToCartRequest("product-1", 0);

        BadApiRequestException exception = assertThrows(
                BadApiRequestException.class,
                () -> cartService.addItemToCart("user-1", request, "log-1")
        );

        assertEquals("Requested quantity is not valid !!", exception.getMessage());
    }

    @Test
    void updateCartItemQuantity_shouldThrowWhenItemDoesNotBelongToUserCart() {
        User user = new User();
        user.setUserId("user-1");

        Cart userCart = new Cart();
        userCart.setCartId("cart-1");

        Cart anotherCart = new Cart();
        anotherCart.setCartId("cart-2");

        Product product = new Product();
        product.setProductId("product-1");
        product.setQuantity(10);
        product.setDiscountedPrice(100);

        CartItem cartItem = CartItem.builder()
                .cart(anotherCart)
                .product(product)
                .quantity(1)
                .build();

        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(userCart));
        when(cartItemRepository.findById(1)).thenReturn(Optional.of(cartItem));

        BadApiRequestException exception = assertThrows(
                BadApiRequestException.class,
                () -> cartService.updateCartItemQuantity("user-1", 1, 2, "log-1")
        );

        assertEquals("Cart item does not belong to this user !!", exception.getMessage());
    }

    @Test
    void removeItemFromCart_shouldDeleteItemWhenFound() {
        CartItem cartItem = new CartItem();
        cartItem.setCartItemId(1);

        when(cartItemRepository.findById(1)).thenReturn(Optional.of(cartItem));

        cartService.removeItemFromCart("user-1", 1, "log-1");

        verify(cartItemRepository).delete(cartItem);
    }
}
