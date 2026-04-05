package com.ecom.salezone.services.impl;

import com.ecom.salezone.enities.User;
import com.ecom.salezone.enums.OtpType;
import com.ecom.salezone.exceptions.InvalidPasswordException;
import com.ecom.salezone.repository.UserRepository;
import com.ecom.salezone.security.JwtService;
import com.ecom.salezone.services.EmailService;
import com.ecom.salezone.services.OtpService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OtpService otpService;

    @Mock
    private JwtService jwtService;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordResetService passwordResetService;

    @Test
    void requestLinkReset_shouldGenerateLinkAndSendEmailForExistingUser() {
        User user = new User();
        user.setEmail("john@salezone.com");
        user.setUserName("john");

        ReflectionTestUtils.setField(passwordResetService, "frontendUrl", "http://localhost:5173");
        when(userRepository.findByEmail("john@salezone.com")).thenReturn(Optional.of(user));
        when(jwtService.generatePwdResetToken(user, "log-1")).thenReturn("token-1");

        passwordResetService.requestLinkReset("john@salezone.com", "log-1");

        verify(emailService).sendPasswordResetLinkEmail(
                "john@salezone.com",
                "john",
                "http://localhost:5173/auth/reset-password?token=token-1",
                "log-1"
        );
    }

    @Test
    void verifyLinkAndReset_shouldThrowWhenTokenTypeIsInvalid() {
        when(jwtService.isPwdResetToken("bad-token")).thenReturn(false);

        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> passwordResetService.verifyLinkAndReset("bad-token", "NewPass@1", "log-1")
        );

        assertEquals("Invalid or expired password reset link", exception.getMessage());
    }

    @Test
    void verifyOtpAndReset_shouldRejectSamePassword() {
        User user = new User();
        user.setEmail("john@salezone.com");
        user.setUserName("john");
        user.setPassword("encoded-old");

        when(userRepository.findByEmail("john@salezone.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("SamePass@1", "encoded-old")).thenReturn(true);

        InvalidPasswordException exception = assertThrows(
                InvalidPasswordException.class,
                () -> passwordResetService.verifyOtpAndReset("john@salezone.com", "123456", "SamePass@1", "log-1")
        );

        assertEquals("New password cannot be the same as your current password", exception.getMessage());
        verify(otpService).verifyOtp("john@salezone.com", "123456", OtpType.PASSWORD_RESET, "log-1");
    }
}
