package com.ecom.salezone.services.impl;
import com.ecom.salezone.enities.User;
import com.ecom.salezone.enums.OtpType;
import com.ecom.salezone.exceptions.InvalidPasswordException;
import com.ecom.salezone.repository.UserRepository;
import com.ecom.salezone.security.JwtService;
import com.ecom.salezone.services.EmailService;
import com.ecom.salezone.services.OtpService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private static final Logger log = LoggerFactory.getLogger(PasswordResetService.class);

    private final UserRepository userRepository;
    private final OtpService otpService;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.cors.front-end-url}")
    private String frontendUrl;

    //  OTP FLOW

    public void requestOtpReset(String email, String logKey) {

        log.info("LogKey: {} - OTP reset requested | email={}", logKey, email);

        userRepository.findByEmail(email).ifPresent(user -> {
            String otp = otpService.generateOtp(email, OtpType.PASSWORD_RESET, logKey);
            emailService.sendPasswordResetOtpEmail(email, user.getUserName(), otp, logKey);
            log.info("LogKey: {} - Password reset OTP sent | email={}", logKey, email);
        });
    }

    public void verifyOtpAndReset(String email, String otp,
                                  String newPassword, String logKey) {

        log.info("LogKey: {} - Verifying OTP reset | email={}", logKey, email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("LogKey: {} - User not found during OTP verify | email={}", logKey, email);
                    return new BadCredentialsException("Invalid request");
                });

        otpService.verifyOtp(email, otp, OtpType.PASSWORD_RESET, logKey);

        saveNewPassword(user, newPassword, logKey);

        emailService.sendPasswordChangedEmail(email, user.getUserName(), logKey);

        log.info("LogKey: {} - Password reset via OTP successful | email={}", logKey, email);
    }

    //  LINK FLOW

    public void requestLinkReset(String email, String logKey) {

        log.info("LogKey: {} - Link reset requested | email={}", logKey, email);

        userRepository.findByEmail(email).ifPresent(user -> {
            String resetToken = jwtService.generatePwdResetToken(user, logKey);
            String resetLink = frontendUrl + "/auth/reset-password?token=" + resetToken;
            emailService.sendPasswordResetLinkEmail(email, user.getUserName(), resetLink, logKey);
            log.info("LogKey: {} - Password reset link sent | email={}", logKey, email);
        });
    }

    public void verifyLinkAndReset(String resetToken,
                                   String newPassword, String logKey) {

        log.info("LogKey: {} - Verifying link reset token", logKey);

        if (!jwtService.isPwdResetToken(resetToken)) {
            log.warn("LogKey: {} - Invalid pwd-reset token type", logKey);
            throw new BadCredentialsException("Invalid or expired password reset link");
        }

        String email = jwtService.getEmail(resetToken);
        log.info("LogKey: {} - Reset token valid | email={}", logKey, email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("LogKey: {} - User not found from reset token | email={}", logKey, email);
                    return new BadCredentialsException("User not found");
                });

        saveNewPassword(user, newPassword, logKey);

        emailService.sendPasswordChangedEmail(user.getEmail(), user.getUserName(), logKey);

        log.info("LogKey: {} - Password reset via link successful | email={}", logKey, email);
    }

    //  SHARED

    private void saveNewPassword(User user, String newPassword, String logKey) {

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new InvalidPasswordException(
                    "New password cannot be the same as your current password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        log.info("LogKey: {} - New password saved | email={}", logKey, user.getEmail());
    }
}