package com.ecom.salezone.repository;

import com.ecom.salezone.enities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing User entities.
 *
 * Provides database operations related to users including:
 * - Fetching users by email
 * - Authenticating users using email and password
 * - Searching users by username
 * - Basic CRUD operations
 *
 * Extends JpaRepository which automatically provides
 * standard database operations.
 *
 * @author : Sandeep Kumar Swain
 * @since : 15-03-2026
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * Fetch a user using email address.
     *
     * @param email user's email
     * @return optional user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Fetch a user using email and password.
     * Used for authentication.
     *
     * @param email user's email
     * @param password user's password
     * @return optional user if credentials match
     */
    Optional<User> findByEmailAndPassword(String email, String password);

    /**
     * Search users whose username contains a specific keyword.
     *
     * @param keywords search keyword
     * @return list of matching users
     */
    List<User> findByUserNameContaining(String keywords);
}