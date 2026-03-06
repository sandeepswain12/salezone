package com.ecom.salezone.services;

import com.ecom.salezone.dtos.AddItemToCartRequest;
import com.ecom.salezone.dtos.CartDto;

public interface CartService {
    //add items to cart:
    //case1: cart for user is not available: we will create the cart and then add the item
    //case2: cart available add the items to cart
    CartDto addItemToCart(String userId, AddItemToCartRequest request, String logkey);

    CartDto updateCartItemQuantity(String userId, int itemId, int quantity, String logkey);

    //remove item from cart:
    void removeItemFromCart(String userId,int cartItem, String logkey);

    //remove all items from cart
    void clearCart(String userId, String logkey);

    CartDto getCartByUser(String userId, String logkey);
}
