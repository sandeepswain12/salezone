package com.ecom.salezone.repository;

import com.ecom.salezone.enities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for CartItem entity.
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
}

