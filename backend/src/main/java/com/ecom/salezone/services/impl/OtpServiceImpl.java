package com.ecom.salezone.services.impl;

import com.ecom.salezone.enities.OtpToken;
import com.ecom.salezone.enums.OtpType;
import com.ecom.salezone.repository.OtpRepository;
import com.ecom.salezone.services.OtpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;

@Service
public class OtpServiceImpl implements OtpService {


    private static final Logger log = LoggerFactory.getLogger(OtpService.class);
    private static final long OTP_EXPIRY_SECONDS = 300L;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Autowired
    private OtpRepository otpRepository;

    /**
     * Deletes all previous unused OTPs for this email+type,
     * generates a fresh 6-digit code and saves it.
     */
    @Transactional
    @Override
    public String generateOtp(String email, OtpType type, String logKey) {

        log.info("LogKey: {} - Generating OTP | email={} type={}", logKey, email, type);

        otpRepository.deleteAllByEmailAndType(email, type);

        String code = String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));

        OtpToken token = OtpToken.builder()
                .email(email)
                .code(code)
                .type(type)
                .used(false)
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(OTP_EXPIRY_SECONDS))
                .build();

        otpRepository.save(token);

        log.info("LogKey: {} - OTP generated and saved | email={} type={}", logKey, email, type);

        return code;
    }

    /**
     * Verifies submitted OTP. Throws BadCredentialsException on any failure.
     * Marks OTP as used on success.
     */
    @Transactional
    public void verifyOtp(String email, String code, OtpType type, String logKey) {

        log.info("LogKey: {} - Verifying OTP | email={} type={}", logKey, email, type);

        OtpToken token = otpRepository
                .findTopByEmailAndTypeAndUsedFalseOrderByCreatedAtDesc(email, type)
                .orElseThrow(() -> {
                    log.warn("LogKey: {} - OTP not found or already used | email={}", logKey, email);
                    return new BadCredentialsException("Invalid or already used OTP");
                });

        if (token.getExpiresAt().isBefore(Instant.now())) {
            log.warn("LogKey: {} - OTP expired | email={}", logKey, email);
            throw new BadCredentialsException("OTP has expired. Please request a new one.");
        }

        if (!token.getCode().equals(code)) {
            log.warn("LogKey: {} - OTP code mismatch | email={}", logKey, email);
            throw new BadCredentialsException("Invalid OTP code");
        }

        token.setUsed(true);
        otpRepository.save(token);

        log.info("LogKey: {} - OTP verified successfully | email={}", logKey, email);
    }

}
