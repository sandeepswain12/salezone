package com.ecom.salezone.controller;

import com.ecom.salezone.dtos.AddItemToCartRequest;
import com.ecom.salezone.dtos.ApiResponseMessage;
import com.ecom.salezone.dtos.CartDto;
import com.ecom.salezone.util.LogKeyGenerator;
import com.ecom.salezone.services.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/salezone/ecom/carts")
public class CartController {

    // Logger for controller-level request tracing
    private static final Logger log = LoggerFactory.getLogger(CartController.class);

    @Autowired
    private CartService cartService;

    /**
     * Add item to cart for a given user
     * URL: POST /salezone/ecom/carts/{userId}
     */
    @PostMapping("/{userId}")
    public ResponseEntity<CartDto> addItemToCart(
            @PathVariable String userId,
            @RequestBody AddItemToCartRequest request) {

        String logkey = LogKeyGenerator.generateLogKey();
        log.info("{} API CALL: Add item to cart | userId={}, request={}",logkey, userId, request);

        // Delegate business logic to service layer
        CartDto cartDto = cartService.addItemToCart(userId, request, logkey);

        log.info("{} Item added/updated successfully in cart | userId={} response={}",logkey, userId, cartDto);

        return new ResponseEntity<>(cartDto, HttpStatus.OK);
    }

    /**
     * Remove a specific item from user's cart
     * URL: DELETE /salezone/ecom/carts/{userId}/items/{itemId}
     */
    @DeleteMapping("/{userId}/items/{itemId}")
    public ResponseEntity<ApiResponseMessage> removeItemFromCart(
            @PathVariable String userId,
            @PathVariable int itemId) {

        String logkey = LogKeyGenerator.generateLogKey();
        log.info("{} API CALL: Remove item from cart | userId={}, itemId={}",logkey, userId, itemId);

        // Call service to remove cart item
        cartService.removeItemFromCart(userId, itemId, logkey);

        ApiResponseMessage response = ApiResponseMessage.builder()
                .message("Item is removed !!")
                .success(true)
                .status(HttpStatus.OK)
                .build();

        log.info("{} Item removed successfully | userId={}, itemId={}",logkey, userId, itemId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Clear all items from user's cart
     * URL: DELETE /salezone/ecom/carts/{userId}
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponseMessage> clearCart(@PathVariable String userId) {

        String logkey = LogKeyGenerator.generateLogKey();
        log.info("{} API CALL: Clear cart | userId={}",logkey, userId);

        // Delegate clear operation to service layer
        cartService.clearCart(userId, logkey);

        ApiResponseMessage response = ApiResponseMessage.builder()
                .message("Now cart is blank !!")
                .success(true)
                .status(HttpStatus.OK)
                .build();

        log.info("{} Cart cleared successfully | userId={}",logkey, userId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Get cart details of a user
     * URL: GET /salezone/ecom/carts/{userId}
     */
    @GetMapping("/{userId}")
    public ResponseEntity<CartDto> getCart(@PathVariable String userId) {

        String logkey = LogKeyGenerator.generateLogKey();
        log.info("{} API CALL: Get cart by user | userId={}",logkey, userId);

        // Fetch cart from service layer
        CartDto cartDto = cartService.getCartByUser(userId, logkey);

        log.info("{} Cart fetched successfully | userId={} response={}",logkey, userId, cartDto);

        return new ResponseEntity<>(cartDto, HttpStatus.OK);
    }
}
