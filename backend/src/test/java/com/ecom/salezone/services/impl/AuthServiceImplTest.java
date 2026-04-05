package com.ecom.salezone.services.impl;

import com.ecom.salezone.dtos.SignupRequestDto;
import com.ecom.salezone.dtos.UserDto;
import com.ecom.salezone.enities.User;
import com.ecom.salezone.enums.OtpType;
import com.ecom.salezone.repository.UserRepository;
import com.ecom.salezone.services.EmailService;
import com.ecom.salezone.services.OtpService;
import com.ecom.salezone.services.UserService;
import com.ecom.salezone.util.EmailTemplates;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OtpService otpService;

    @Mock
    private EmailService emailService;

    @Mock
    private EmailTemplates emailTemplate;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void registerUser_shouldDeactivateUserGenerateOtpAndSendEmail() {
        SignupRequestDto request = SignupRequestDto.builder()
                .email("john@salezone.com")
                .userName("john")
                .password("Password@1")
                .build();
        UserDto createdUser = UserDto.builder().userId("user-1").build();
        User user = new User();
        user.setUserId("user-1");
        user.setEmail("john@salezone.com");
        user.setUserName("john");
        user.setActive(true);
        user.setEmailVerified(true);

        when(userService.createUser(request, "log-1")).thenReturn(createdUser);
        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));
        when(otpService.generateOtp("john@salezone.com", OtpType.REGISTRATION, "log-1"))
                .thenReturn("123456");

        UserDto result = authService.registerUser(request, "log-1");

        assertEquals("user-1", result.getUserId());
        assertFalse(user.getActive());
        assertFalse(user.getEmailVerified());
        verify(userRepository).save(user);
        verify(emailService).sendOtpEmail("john@salezone.com", "john", "123456", OtpType.REGISTRATION, "log-1");
    }
}
