package com.ecom.salezone.controller;

import com.ecom.salezone.dtos.AddToWishlistRequest;
import com.ecom.salezone.dtos.ApiResponseMessage;
import com.ecom.salezone.dtos.WishlistDto;
import com.ecom.salezone.services.WishlistService;
import com.ecom.salezone.util.LogKeyGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/salezone/ecom/wishlist")
public class WishlistController {

    private static final Logger log =
            LoggerFactory.getLogger(WishlistController.class);

    @Autowired
    private WishlistService wishlistService;

    /* Add to Wishlist */
    @PostMapping("/{userId}")
    public ResponseEntity<WishlistDto> addToWishlist(
            @PathVariable String userId,
            @RequestBody AddToWishlistRequest request) {

        String logkey = LogKeyGenerator.generateLogKey();

        log.info("LogKey: {} - Add to wishlist request received | userId={} payload={}",
                logkey, userId, request);

        WishlistDto dto =
                wishlistService.addToWishlist(userId, request, logkey);

        log.info("LogKey: {} - Product added to wishlist successfully | userId={} response={}",
                logkey, userId, dto);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    /* Get Wishlist */
    @GetMapping("/{userId}")
    public ResponseEntity<WishlistDto> getWishlist(
            @PathVariable String userId) {

        String logkey = LogKeyGenerator.generateLogKey();

        log.info("LogKey: {} - Get wishlist request received | userId={}",
                logkey, userId);

        WishlistDto dto =
                wishlistService.getWishlist(userId, logkey);

        log.info("LogKey: {} - Wishlist fetched successfully | userId={} response={}",
                logkey, userId, dto);

        return ResponseEntity.ok(dto);
    }

    /* Remove from Wishlist */
    @DeleteMapping("/{userId}/{productId}")
    public ResponseEntity<ApiResponseMessage> removeFromWishlist(
            @PathVariable String userId,
            @PathVariable String productId) {

        String logkey = LogKeyGenerator.generateLogKey();

        log.warn("LogKey: {} - Remove from wishlist request received | userId={} productId={}",
                logkey, userId, productId);

        wishlistService.removeFromWishlist(userId, productId, logkey);

        ApiResponseMessage response = ApiResponseMessage.builder()
                .message("Removed from wishlist")
                .success(true)
                .status(HttpStatus.OK)
                .build();

        log.info("LogKey: {} - Product removed from wishlist successfully | userId={} productId={}",
                logkey, userId, productId);

        return ResponseEntity.ok(response);
    }
}