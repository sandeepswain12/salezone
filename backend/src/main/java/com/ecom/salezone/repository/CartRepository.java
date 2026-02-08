package com.ecom.salezone.repository;

import com.ecom.salezone.enities.Cart;
import com.ecom.salezone.enities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Cart entity.
 */
@Repository
public interface CartRepository extends JpaRepository<Cart, String> {

    // Fetch cart by user
    Optional<Cart> findByUser(User user);

}
