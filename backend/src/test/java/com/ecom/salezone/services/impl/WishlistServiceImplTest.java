package com.ecom.salezone.services.impl;

import com.ecom.salezone.dtos.AddToWishlistRequest;
import com.ecom.salezone.dtos.ProductDto;
import com.ecom.salezone.dtos.WishlistDto;
import com.ecom.salezone.enities.Product;
import com.ecom.salezone.enities.User;
import com.ecom.salezone.enities.Wishlist;
import com.ecom.salezone.exceptions.BadApiRequestException;
import com.ecom.salezone.exceptions.ResourceNotFoundException;
import com.ecom.salezone.repository.ProductRepository;
import com.ecom.salezone.repository.UserRepository;
import com.ecom.salezone.repository.WishlistRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WishlistServiceImplTest {

    private static final String USER_ID = "user-1";
    private static final String PRODUCT_ID = "product-1";
    private static final String LOG_KEY = "log-123";

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private WishlistServiceImpl wishlistService;

    @Test
    void addToWishlist_shouldSaveWishlistAndReturnWishlistDto() {
        User user = createUser(USER_ID);
        Product product = createProduct(PRODUCT_ID, "Laptop");
        AddToWishlistRequest request = new AddToWishlistRequest(PRODUCT_ID);
        ProductDto productDto = ProductDto.builder()
                .productId(PRODUCT_ID)
                .title("Laptop")
                .build();
        Wishlist savedWishlist = new Wishlist();
        savedWishlist.setUser(user);
        savedWishlist.setProduct(product);

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(wishlistRepository.findByUserAndProduct(user, product)).thenReturn(Optional.empty());
        when(wishlistRepository.save(any(Wishlist.class))).thenReturn(savedWishlist);
        when(wishlistRepository.findByUser(user)).thenReturn(List.of(savedWishlist));
        when(mapper.map(product, ProductDto.class)).thenReturn(productDto);

        WishlistDto result = wishlistService.addToWishlist(USER_ID, request, LOG_KEY);

        assertNotNull(result);
        assertEquals(USER_ID, result.getUserId());
        assertEquals(1, result.getProducts().size());
        assertEquals(PRODUCT_ID, result.getProducts().getFirst().getProductId());

        verify(wishlistRepository).save(any(Wishlist.class));
    }

    @Test
    void addToWishlist_shouldThrowExceptionWhenProductAlreadyExists() {
        User user = createUser(USER_ID);
        Product product = createProduct(PRODUCT_ID, "Laptop");
        AddToWishlistRequest request = new AddToWishlistRequest(PRODUCT_ID);
        Wishlist existingWishlist = new Wishlist();
        existingWishlist.setUser(user);
        existingWishlist.setProduct(product);

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(wishlistRepository.findByUserAndProduct(user, product)).thenReturn(Optional.of(existingWishlist));

        BadApiRequestException exception = assertThrows(
                BadApiRequestException.class,
                () -> wishlistService.addToWishlist(USER_ID, request, LOG_KEY)
        );

        assertEquals("Product already in wishlist !!", exception.getMessage());
        verify(wishlistRepository, never()).save(any(Wishlist.class));
    }

    @Test
    void getWishlist_shouldThrowExceptionWhenUserDoesNotExist() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> wishlistService.getWishlist(USER_ID, LOG_KEY)
        );

        assertEquals("User not found !!", exception.getMessage());
    }

    @Test
    void removeFromWishlist_shouldDeleteWishlistEntry() {
        User user = createUser(USER_ID);
        Product product = createProduct(PRODUCT_ID, "Laptop");

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));

        wishlistService.removeFromWishlist(USER_ID, PRODUCT_ID, LOG_KEY);

        verify(wishlistRepository).deleteByUserAndProduct(user, product);
    }

    private User createUser(String userId) {
        User user = new User();
        user.setUserId(userId);
        user.setUserName("Test User");
        user.setEmail("test@salezone.com");
        return user;
    }

    private Product createProduct(String productId, String title) {
        Product product = new Product();
        product.setProductId(productId);
        product.setTitle(title);
        product.setDiscountedPrice(1000);
        product.setQuantity(5);
        return product;
    }
}
