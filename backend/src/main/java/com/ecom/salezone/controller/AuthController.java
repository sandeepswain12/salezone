package com.ecom.salezone.controller;

import com.ecom.salezone.dtos.SignupRequestDto;
import com.ecom.salezone.dtos.UserDto;
import com.ecom.salezone.enities.User;
import com.ecom.salezone.helper.LogKeyGenerator;
import com.ecom.salezone.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/salezone/ecom/auth")
@CrossOrigin(origins = "http://localhost:5173/")
public class AuthController {

    @Autowired
    private UserService userService;

    // 🔓 PUBLIC SIGNUP
    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(@RequestBody SignupRequestDto userDto) {
        String logkey = LogKeyGenerator.generateLogKey();
        UserDto createdUser = userService.createUser(userDto, logkey);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    // 🔐 BASIC AUTH LOGIN TEST
    @GetMapping("/login")
    public ResponseEntity<String> login(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok("Login successful for " + user.getEmail());
    }
}

