package com.ecom.salezone.repository;

import com.ecom.salezone.enities.RefreshToken;
import com.ecom.salezone.enities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing RefreshToken entities.
 *
 * Provides database operations related to refresh tokens including:
 * - Fetching refresh tokens by JTI (JWT ID)
 * - Deleting refresh tokens associated with a user
 * - Basic CRUD operations
 *
 * Extends JpaRepository which automatically provides
 * standard database operations.
 *
 * @author : Sandeep Kumar Swain
 * @since : 15-03-2026
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    /**
     * Find refresh token using JWT ID (JTI).
     *
     * @param jti unique identifier of the JWT
     * @return optional refresh token
     */
    Optional<RefreshToken> findByJti(String jti);

    /**
     * Delete all refresh tokens belonging to a specific user.
     *
     * Useful during logout or when invalidating user sessions.
     *
     * @param user user entity
     */
    void deleteByUser(User user);
}