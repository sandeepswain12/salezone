package com.ecom.salezone.services.impl;

import com.ecom.salezone.dtos.SignupRequestDto;
import com.ecom.salezone.dtos.UserDto;
import com.ecom.salezone.services.AuthService;
import com.ecom.salezone.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDto registerUser(SignupRequestDto userDto, String logkey) {
        passwordEncoder.encode(userDto.getPassword());
        return userService.createUser(userDto,logkey);
    }
}
