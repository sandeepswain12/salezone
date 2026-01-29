package com.ecom.salezone.repository;

import com.ecom.salezone.enities.Cart;
import com.ecom.salezone.enities.User;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart,String> {

    Optional<Cart> findByUser(User user);

}
