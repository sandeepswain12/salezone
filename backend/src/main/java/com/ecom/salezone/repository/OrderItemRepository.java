package com.ecom.salezone.repository;

import com.ecom.salezone.enities.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem,String> {
}
