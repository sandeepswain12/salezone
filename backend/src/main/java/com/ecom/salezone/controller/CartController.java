package com.ecom.salezone.controller;

import com.ecom.salezone.dtos.AddItemToCartRequest;
import com.ecom.salezone.dtos.ApiResponseMessage;
import com.ecom.salezone.dtos.CartDto;
import com.ecom.salezone.util.LogKeyGenerator;
import com.ecom.salezone.services.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
/**
 * CartController handles all shopping cart related operations
 * in the SaleZone E-commerce system.
 *
 * This controller provides APIs for:
 * - Adding items to the cart
 * - Removing items from the cart
 * - Updating cart item quantity
 * - Clearing the entire cart
 * - Fetching the user's cart details
 *
 * Features:
 * - Each user has a unique cart
 * - Cart items can be added, updated or removed
 * - Quantity management for cart items
 * - Complete cart clearing option
 *
 * Security:
 * - APIs typically require authenticated users
 * - Cart operations are user specific
 *
 * @author : Sandeep Kumar Swain
 * @version : 1.0
 * @since : 15-03-2026
 */

@Tag(
        name = "Cart APIs",
        description = "APIs for managing user shopping carts in the SaleZone system"
)
@RestController
@RequestMapping("/salezone/ecom/carts")
public class CartController {

    private static final Logger log =
            LoggerFactory.getLogger(CartController.class);

    @Autowired
    private CartService cartService;

    @Operation(
            summary = "Add item to cart",
            description = "Adds a product to the user's cart or updates the quantity if the item already exists."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item added to cart successfully"),
            @ApiResponse(responseCode = "404", description = "User or product not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload")
    })
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

    @Operation(
            summary = "Remove item from cart",
            description = "Removes a specific item from the user's cart."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item removed successfully"),
            @ApiResponse(responseCode = "404", description = "Cart item not found")
    })
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

    @Operation(
            summary = "Update cart item quantity",
            description = "Updates the quantity of a specific item in the user's cart."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart item quantity updated successfully"),
            @ApiResponse(responseCode = "404", description = "Cart item not found")
    })
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

    @Operation(
            summary = "Clear user cart",
            description = "Removes all items from the user's shopping cart."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart cleared successfully"),
            @ApiResponse(responseCode = "404", description = "Cart not found")
    })
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

    @Operation(
            summary = "Get user cart",
            description = "Fetches the complete cart details of a specific user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Cart not found")
    })
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