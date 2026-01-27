package com.ecom.salezone.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/salezone/ecom/auth")
public class AuthController {

    @GetMapping("/login")
    public ResponseEntity<String> login() {
        return ResponseEntity.ok("Login successful");
    }
}
