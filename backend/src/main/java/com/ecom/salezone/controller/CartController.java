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

    private static final Logger log =
            LoggerFactory.getLogger(CartController.class);

    @Autowired
    private CartService cartService;

    // ================= ADD ITEM TO CART =================
    @PostMapping("/{userId}")
    public ResponseEntity<CartDto> addItemToCart(
            @PathVariable String userId,
            @RequestBody AddItemToCartRequest request) {

        String logkey = LogKeyGenerator.generateLogKey();
        log.info("LogKey: {} - Add item to cart request received | userId={} payload={}",
                logkey, userId, request);

        CartDto cartDto =
                cartService.addItemToCart(userId, request, logkey);

        log.info("LogKey: {} - Item added/updated successfully in cart | userId={} payload={}",
                logkey, userId, cartDto);

        return new ResponseEntity<>(cartDto, HttpStatus.OK);
    }

    // ================= REMOVE ITEM FROM CART =================
    @DeleteMapping("/{userId}/items/{itemId}")
    public ResponseEntity<ApiResponseMessage> removeItemFromCart(
            @PathVariable String userId,
            @PathVariable int itemId) {

        String logkey = LogKeyGenerator.generateLogKey();
        log.warn("LogKey: {} - Remove item from cart request received | userId={} itemId={}",
                logkey, userId, itemId);

        cartService.removeItemFromCart(userId, itemId, logkey);

        ApiResponseMessage response =
                ApiResponseMessage.builder()
                        .message("Item is removed !!")
                        .success(true)
                        .status(HttpStatus.OK)
                        .build();

        log.info("LogKey: {} - Item removed successfully from cart | userId={} itemId={}",
                logkey, userId, itemId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // ================= UPDATE CART ITEM QUANTITY =================
    @PutMapping("/{userId}/items/{itemId}")
    public ResponseEntity<CartDto> updateCartItemQuantity(
            @PathVariable String userId,
            @PathVariable int itemId,
            @RequestParam int quantity) {

        String logkey = LogKeyGenerator.generateLogKey();
        log.info("LogKey: {} - Update cart item quantity request received | userId={} itemId={} quantity={}",
                logkey, userId, itemId, quantity);

        CartDto cartDto =
                cartService.updateCartItemQuantity(userId, itemId, quantity, logkey);

        log.info("LogKey: {} - Cart item quantity updated successfully | userId={} itemId={}",
                logkey, userId, itemId);

        return new ResponseEntity<>(cartDto, HttpStatus.OK);
    }

    // ================= CLEAR CART =================
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponseMessage> clearCart(
            @PathVariable String userId) {

        String logkey = LogKeyGenerator.generateLogKey();
        log.warn("LogKey: {} - Clear cart request received | userId={}",
                logkey, userId);

        cartService.clearCart(userId, logkey);

        ApiResponseMessage response =
                ApiResponseMessage.builder()
                        .message("Now cart is blank !!")
                        .success(true)
                        .status(HttpStatus.OK)
                        .build();

        log.info("LogKey: {} - Cart cleared successfully | userId={}",
                logkey, userId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // ================= GET CART =================
    @GetMapping("/{userId}")
    public ResponseEntity<CartDto> getCart(
            @PathVariable String userId) {

        String logkey = LogKeyGenerator.generateLogKey();
        log.info("LogKey: {} - Get cart request received | userId={}",
                logkey, userId);

        CartDto cartDto =
                cartService.getCartByUser(userId, logkey);

        log.info("LogKey: {} - Cart fetched successfully | userId={} payload={}",
                logkey, userId, cartDto);

        return new ResponseEntity<>(cartDto, HttpStatus.OK);
    }
}