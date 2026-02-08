package com.ecom.salezone.repository;

import com.ecom.salezone.enities.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Repository for OrderItem entity.
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem,Integer> {
}
