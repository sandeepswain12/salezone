package com.ecom.salezone.repository;

import com.ecom.salezone.enities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem,Integer> {
}
