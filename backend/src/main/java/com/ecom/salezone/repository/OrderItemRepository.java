package com.ecom.salezone.repository;

import com.ecom.salezone.enities.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing OrderItem entities.
 *
 * Provides database operations for order items such as:
 * - Adding products to orders
 * - Updating order item details
 * - Removing order items
 * - Fetching items belonging to specific orders
 *
 * Extends JpaRepository which automatically provides
 * standard CRUD operations.
 *
 * @author : Sandeep Kumar Swain
 * @since : 15-03-2026
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

}