package com.ecom.salezone.services;

import com.ecom.salezone.dtos.AddItemToCartRequest;
import com.ecom.salezone.dtos.CartDto;

/**
 * CartService defines business operations related to
 * shopping cart management in the SaleZone e-commerce system.
 *
 * Responsibilities:
 * - Adding items to the user's cart
 * - Updating cart item quantity
 * - Removing items from the cart
 * - Clearing the cart
 * - Fetching cart details for a user
 *
 * Cart Behavior:
 * - If a cart does not exist for a user, a new cart will be created.
 * - If a cart already exists, items will be added or updated accordingly.
 *
 * @author : Sandeep Kumar Swain
 * @version : 1.0
 * @since : 15-03-2026
 */
public interface CartService {

    /**
     * Adds an item to the user's cart.
     *
     * Behavior:
     * - If the cart does not exist, a new cart will be created.
     * - If the item already exists in the cart, its quantity may be updated.
     *
     * @param userId   ID of the user
     * @param request  request containing product and quantity details
     * @param logkey   unique request identifier used for log tracing
     *
     * @return updated CartDto containing cart details
     */
    CartDto addItemToCart(String userId, AddItemToCartRequest request, String logkey);

    /**
     * Updates the quantity of an existing cart item.
     *
     * @param userId   ID of the user
     * @param itemId   cart item identifier
     * @param quantity new quantity to update
     * @param logkey   unique request identifier used for log tracing
     *
     * @return updated CartDto containing updated cart details
     */
    CartDto updateCartItemQuantity(String userId, int itemId, int quantity, String logkey);

    /**
     * Removes a specific item from the user's cart.
     *
     * @param userId   ID of the user
     * @param cartItem ID of the cart item to remove
     * @param logkey   unique request identifier used for log tracing
     */
    void removeItemFromCart(String userId, int cartItem, String logkey);

    /**
     * Clears all items from the user's cart.
     *
     * @param userId ID of the user
     * @param logkey unique request identifier used for log tracing
     */
    void clearCart(String userId, String logkey);

    /**
     * Fetches the cart details of a specific user.
     *
     * @param userId ID of the user
     * @param logkey unique request identifier used for log tracing
     *
     * @return CartDto containing the user's cart details
     */
    CartDto getCartByUser(String userId, String logkey);
}