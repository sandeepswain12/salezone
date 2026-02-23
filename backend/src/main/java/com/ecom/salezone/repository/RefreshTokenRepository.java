package com.ecom.salezone.repository;

import com.ecom.salezone.enities.RefreshToken;
import com.ecom.salezone.enities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByJti(String jti);

    void deleteByUser(User user);
}
