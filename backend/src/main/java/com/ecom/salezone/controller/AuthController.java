package com.ecom.salezone.controller;

import com.ecom.salezone.dtos.LoginRequest;
import com.ecom.salezone.dtos.SignupRequestDto;
import com.ecom.salezone.dtos.UserDto;
import com.ecom.salezone.enities.User;
import com.ecom.salezone.repository.UserRepository;
import com.ecom.salezone.services.AuthService;
import com.ecom.salezone.util.LogKeyGenerator;
import com.ecom.salezone.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/salezone/ecom/auth")
@CrossOrigin(origins = "http://localhost:5173/")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private AuthenticationManager authenticationManager;


    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(@RequestBody SignupRequestDto userDto) {
        String logkey = LogKeyGenerator.generateLogKey();
        UserDto createdUser = authService.registerUser(userDto, logkey);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }


    @GetMapping("/login")
    public ResponseEntity<String> login(LoginRequest loginRequest, HttpServletResponse response) {
        Authentication authentication = authenticate(loginRequest);
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
        if (!user.isEnabled()){
            throw new DisabledException("User is disabled");
        }
    }

    private Authentication authenticate(LoginRequest loginRequest) {
        try{
            return  authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword()));
        }catch (Exception e) {
            throw new BadCredentialsException("Invalid Username or Password !!");
        }
    }
}

