package com.ecom.salezone.services.impl;

import com.ecom.salezone.controller.AuthController;
import com.ecom.salezone.dtos.SignupRequestDto;
import com.ecom.salezone.dtos.UserDto;
import com.ecom.salezone.services.AuthService;
import com.ecom.salezone.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDto registerUser(SignupRequestDto userDto, String logkey) {
        log.info("LogKey: {} - Entry into register user with userDetails : {}", logkey,userDto);
        return userService.createUser(userDto,logkey);
    }
}
