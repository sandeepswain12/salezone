package com.ecom.salezone.repository;

import com.ecom.salezone.enities.OtpToken;
import com.ecom.salezone.enums.OtpType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OtpToken, String> {

    Optional<OtpToken> findTopByEmailAndTypeAndUsedFalseOrderByCreatedAtDesc(
            String email, OtpType type
    );

    void deleteAllByEmailAndType(String email, OtpType type);
}