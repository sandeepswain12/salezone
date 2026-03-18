package com.ecom.salezone.repository;

import com.ecom.salezone.enities.Cart;
import com.ecom.salezone.enities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing Cart entities.
 *
 * Provides CRUD operations for carts and allows fetching
 * carts associated with specific users.
 *
 * Each user typically has a single cart which contains
 * items added during shopping.
 *
 * @author : Sandeep Kumar Swain
 * @since : 15-03-2026
 */
@Repository
public interface CartRepository extends JpaRepository<Cart, String> {

    /**
     * Fetch cart associated with a specific user.
     *
     * @param user user entity
     * @return optional cart of the user
     */
    Optional<Cart> findByUser(User user);

}