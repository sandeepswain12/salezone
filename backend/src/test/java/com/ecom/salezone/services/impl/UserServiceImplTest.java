package com.ecom.salezone.services.impl;

import com.ecom.salezone.dtos.SignupRequestDto;
import com.ecom.salezone.dtos.UpdateUserRequest;
import com.ecom.salezone.dtos.UserDto;
import com.ecom.salezone.enities.Role;
import com.ecom.salezone.enities.User;
import com.ecom.salezone.enums.Provider;
import com.ecom.salezone.exceptions.ResourceNotFoundException;
import com.ecom.salezone.repository.RefreshTokenRepository;
import com.ecom.salezone.repository.RoleRepository;
import com.ecom.salezone.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_shouldEncodePasswordAssignRoleAndReturnDto() {
        SignupRequestDto request = SignupRequestDto.builder()
                .userName("john")
                .email("john@salezone.com")
                .password("Password@1")
                .provider(Provider.LOCAL)
                .build();
        User mappedUser = new User();
        mappedUser.setRoles(new HashSet<>());
        Role role = new Role("ROLE_USER", "USER");
        User savedUser = new User();
        savedUser.setUserId("user-1");
        UserDto response = UserDto.builder().userId("user-1").build();

        when(passwordEncoder.encode("Password@1")).thenReturn("encoded-password");
        when(modelMapper.map(request, User.class)).thenReturn(mappedUser);
        when(roleRepository.findById("ROLE_USER")).thenReturn(Optional.of(role));
        when(userRepository.save(mappedUser)).thenReturn(savedUser);
        when(modelMapper.map(savedUser, UserDto.class)).thenReturn(response);

        UserDto result = userService.createUser(request, "log-1");

        assertNotNull(request.getUserId());
        assertEquals("encoded-password", request.getPassword());
        assertEquals(Provider.LOCAL, mappedUser.getProvider());
        assertEquals(1, mappedUser.getRoles().size());
        assertEquals("user-1", result.getUserId());
    }

    @Test
    void updateUser_shouldThrowWhenUserDoesNotExist() {
        when(userRepository.findById("missing")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.updateUser(new UpdateUserRequest(), "missing", "log-1")
        );

        assertEquals("User not found", exception.getMessage());
    }
}
