package com.example.geosvc.controller;

import com.example.geosvc.service.AuthService;
import com.example.geosvc.service.AuthService.AuthenticationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        AuthenticationResponse response = authService.login(
            loginRequest.getUsername(),
            loginRequest.getPassword()
        );
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response.getData());
        } else {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response.getError());
        }
    }
} 