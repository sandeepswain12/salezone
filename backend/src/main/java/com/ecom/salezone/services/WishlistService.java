package com.ecom.salezone.services;

import com.ecom.salezone.dtos.AddToWishlistRequest;
import com.ecom.salezone.dtos.WishlistDto;

public interface WishlistService {

    WishlistDto addToWishlist(String userId, AddToWishlistRequest request, String logkey);

    WishlistDto getWishlist(String userId, String logkey);

    void removeFromWishlist(String userId, String productId, String logkey);
}
