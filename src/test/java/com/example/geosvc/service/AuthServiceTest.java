package com.example.geosvc.service;

import com.example.geosvc.dto.LoginRequest;
import com.example.geosvc.dto.LoginResponse;
import com.example.geosvc.exception.AuthenticationException;
import com.example.geosvc.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        userDetails = new User("admin", "password", Collections.emptyList());
    }

    @Test
    void login_ValidCredentials_ReturnsToken() {
        // Given
        LoginRequest request = new LoginRequest("test@example.com", "password");
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        
        when(authenticationManager.authenticate(any()))
            .thenReturn(authentication);
        when(jwtService.generateToken(any(UserDetails.class)))
            .thenReturn("test-token");

        // When
        LoginResponse response = authService.login(request);

        // Then
        assertNotNull(response);
        assertEquals("test-token", response.getToken());
        verify(authenticationManager).authenticate(any());
        verify(jwtService).generateToken(any(UserDetails.class));
    }

    @Test
    void login_InvalidCredentials_ThrowsException() {
        // Arrange
        LoginRequest request = new LoginRequest("admin", "wrongpassword");
        when(authenticationManager.authenticate(any()))
            .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        AuthenticationException exception = assertThrows(AuthenticationException.class, () -> {
            authService.login(request);
        });
        
        assertEquals("Incorrect username or password", exception.getMessage());
        verify(authenticationManager, times(1)).authenticate(any());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void login_AuthenticationException_ThrowsException() {
        // Arrange
        LoginRequest request = new LoginRequest("admin", "password");
        when(authenticationManager.authenticate(any()))
            .thenThrow(new DisabledException("Authentication failed"));

        // Act & Assert
        AuthenticationException exception = assertThrows(AuthenticationException.class, () -> {
            authService.login(request);
        });
        
        assertEquals("Incorrect username or password", exception.getMessage());
        verify(authenticationManager, times(1)).authenticate(any());
        verify(jwtService, never()).generateToken(any());
    }
} 