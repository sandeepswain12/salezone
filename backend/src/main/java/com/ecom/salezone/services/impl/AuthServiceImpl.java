package com.ecom.salezone.services.impl;

import com.ecom.salezone.dtos.SignupRequestDto;
import com.ecom.salezone.dtos.UserDto;
import com.ecom.salezone.enities.User;
import com.ecom.salezone.enums.OtpType;
import com.ecom.salezone.repository.UserRepository;
import com.ecom.salezone.services.AuthService;
import com.ecom.salezone.services.EmailService;
import com.ecom.salezone.services.OtpService;
import com.ecom.salezone.services.UserService;
import com.ecom.salezone.util.EmailTemplates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Implementation of AuthService.
 *
 * Provides authentication related business logic
 * for the SaleZone E-commerce system.
 *
 * Registration flow:
 * 1. Create user via UserService (isActive=true by default)
 * 2. Immediately flip isActive=false (account locked until OTP verified)
 * 3. Generate 6-digit OTP and save to DB
 * 4. Send OTP email via SendGrid
 */
@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpService otpService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailTemplates emailTemplate;

    @Override
    public UserDto registerUser(SignupRequestDto userDto, String logKey) {

        log.info("LogKey: {} - Entry into registerUser | email={}", logKey, userDto.getEmail());

        // create user (UserService handles validation, password hashing, role assignment)
        UserDto createdUser = userService.createUser(userDto, logKey);
        log.info("LogKey: {} - User created by UserService | userId={}", logKey, createdUser.getUserId());

        // flip isActive=false — account is locked until email OTP is verified
        User user = userRepository.findById(createdUser.getUserId())
                .orElseThrow(() -> {
                    log.error("LogKey: {} - User not found after creation | userId={}",
                            logKey, createdUser.getUserId());
                    return new RuntimeException("User not found after registration");
                });

        user.setActive(false);
        user.setEmailVerified(false);
        userRepository.save(user);
        log.info("LogKey: {} - User account deactivated pending OTP | email={}", logKey, user.getEmail());

        // generate OTP and save to DB
        String otp = otpService.generateOtp(user.getEmail(), OtpType.REGISTRATION, logKey);
        log.info("LogKey: {} - Registration OTP generated | email={}", logKey, user.getEmail());

        // send OTP email via SendGrid
        emailService.sendOtpEmail(
                user.getEmail(),
                user.getUserName(),
                otp,
                OtpType.REGISTRATION,
                logKey
        );
        log.info("LogKey: {} - Registration OTP email sent | email={}", logKey, user.getEmail());

        return createdUser;
    }
}