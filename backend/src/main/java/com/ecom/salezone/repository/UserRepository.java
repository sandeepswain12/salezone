package com.ecom.salezone.repository;

import com.ecom.salezone.enities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for User entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    // Fetch user by email
    Optional<User> findByEmail(String email);

    // Fetch user by email and password
    Optional<User> findByEmailAndPassword(String email, String password);

    // Search users by username keyword
    List<User> findByUserNameContaining(String keywords);
}

