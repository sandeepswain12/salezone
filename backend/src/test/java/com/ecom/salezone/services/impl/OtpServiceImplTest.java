package com.ecom.salezone.services.impl;

import com.ecom.salezone.enities.OtpToken;
import com.ecom.salezone.enums.OtpType;
import com.ecom.salezone.repository.OtpRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OtpServiceImplTest {

    @Mock
    private OtpRepository otpRepository;

    @InjectMocks
    private OtpServiceImpl otpService;

    @Test
    void generateOtp_shouldDeleteOldTokensAndSaveNewSixDigitCode() {
        String code = otpService.generateOtp("john@salezone.com", OtpType.REGISTRATION, "log-1");

        ArgumentCaptor<OtpToken> captor = ArgumentCaptor.forClass(OtpToken.class);
        verify(otpRepository).deleteAllByEmailAndType("john@salezone.com", OtpType.REGISTRATION);
        verify(otpRepository).save(captor.capture());

        assertEquals(6, code.length());
        assertTrue(code.matches("\\d{6}"));
        assertEquals(code, captor.getValue().getCode());
        assertFalse(captor.getValue().isUsed());
    }

    @Test
    void verifyOtp_shouldMarkTokenUsedWhenCodeMatches() {
        OtpToken token = OtpToken.builder()
                .email("john@salezone.com")
                .code("123456")
                .type(OtpType.LOGIN)
                .used(false)
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(300))
                .build();

        when(otpRepository.findTopByEmailAndTypeAndUsedFalseOrderByCreatedAtDesc(
                "john@salezone.com", OtpType.LOGIN)).thenReturn(Optional.of(token));

        otpService.verifyOtp("john@salezone.com", "123456", OtpType.LOGIN, "log-1");

        assertTrue(token.isUsed());
        verify(otpRepository).save(token);
    }

    @Test
    void verifyOtp_shouldThrowWhenTokenIsExpired() {
        OtpToken token = OtpToken.builder()
                .email("john@salezone.com")
                .code("123456")
                .type(OtpType.LOGIN)
                .used(false)
                .createdAt(Instant.now().minusSeconds(400))
                .expiresAt(Instant.now().minusSeconds(1))
                .build();

        when(otpRepository.findTopByEmailAndTypeAndUsedFalseOrderByCreatedAtDesc(
                "john@salezone.com", OtpType.LOGIN)).thenReturn(Optional.of(token));

        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> otpService.verifyOtp("john@salezone.com", "123456", OtpType.LOGIN, "log-1")
        );

        assertEquals("OTP has expired. Please request a new one.", exception.getMessage());
    }
}
