package com.ecom.salezone.repository;

import com.ecom.salezone.enities.Order;
import com.ecom.salezone.enities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


/**
 * Repository for Order entity.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    // Fetch all orders of a specific user
    List<Order> findByUser(User user);

    List<Order> findByUserOrderByOrderedDateDesc(User user);

}

