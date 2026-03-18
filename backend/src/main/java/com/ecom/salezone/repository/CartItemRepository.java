package com.ecom.salezone.repository;

import com.ecom.salezone.enities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing CartItem entities.
 *
 * Provides CRUD operations for cart items such as:
 * - Adding items to cart
 * - Updating cart item quantity
 * - Removing items from cart
 * - Fetching cart items
 *
 * This repository extends JpaRepository which provides
 * standard database operations without requiring manual implementation.
 *
 * @author : Sandeep Kumar Swain
 * @since : 15-03-2026
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

}