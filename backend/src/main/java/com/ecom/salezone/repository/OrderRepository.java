package com.ecom.salezone.repository;

import com.ecom.salezone.enities.Order;
import com.ecom.salezone.enities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,String> {

    List<Order> findByUser(User user);

}
