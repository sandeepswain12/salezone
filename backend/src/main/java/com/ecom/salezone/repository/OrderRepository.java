package com.ecom.salezone.repository;

import com.ecom.salezone.enities.Order;
import com.ecom.salezone.enities.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing Order entities.
 *
 * Provides database operations for orders including:
 * - Fetching orders of users
 * - Sorting orders by date
 * - Basic CRUD operations
 *
 * Extends JpaRepository which automatically provides
 * standard database operations.
 *
 * @author : Sandeep Kumar Swain
 * @since : 15-03-2026
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    /**
     * Fetch all orders belonging to a specific user.
     *
     * @param user user entity
     * @return list of orders
     */
    @EntityGraph(attributePaths = {"orderItems", "orderItems.product"})
    List<Order> findByUser(User user);

    /**
     * Fetch all orders of a user sorted by newest first.
     *
     * @param user user entity
     * @return list of orders ordered by date descending
     */
    @EntityGraph(attributePaths = {"orderItems", "orderItems.product"})
    List<Order> findByUserOrderByOrderedDateDesc(User user);

    @Query(value = """
    SELECT DATE(o.ordered_date), SUM(o.order_amount)
    FROM orders o
    WHERE o.o_status = 'DELIVERED'
    GROUP BY DATE(o.ordered_date)
    ORDER BY DATE(o.ordered_date)
""", nativeQuery = true)
    List<Object[]> getRevenueData();

    @Query(value = """
    SELECT DATE(o.ordered_date), COUNT(*)
    FROM orders o
    GROUP BY DATE(o.ordered_date)
    ORDER BY DATE(o.ordered_date)
""", nativeQuery = true)
    List<Object[]> getOrdersData();

    @Query(value = """
    SELECT SUM(o.order_amount)
    FROM orders o
    WHERE o.order_status = 'DELIVERED'
""", nativeQuery = true)
    Double getTotalRevenue();

}