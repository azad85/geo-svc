package com.example.geosvc.controller;

import com.example.geosvc.dto.LoginRequest;
import com.example.geosvc.dto.LoginResponse;
import com.example.geosvc.dto.ErrorResponse;
import com.example.geosvc.exception.AuthenticationException;
import com.example.geosvc.exception.GlobalExceptionHandler;
import com.example.geosvc.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private LoginRequest validRequest;
    private LoginRequest invalidRequest;

    @BeforeEach
    void setUp() {
        validRequest = new LoginRequest("test@example.com", "password");
        invalidRequest = new LoginRequest("test@example.com", "wrongpassword");
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    }

    @Test
    void login_ValidCredentials_ReturnsSuccessResponse() throws Exception {
        // Arrange
        LoginResponse expectedResponse = new LoginResponse("test-token");
        when(authService.login(any(LoginRequest.class))).thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType("application/json")
                .content("{\"username\":\"test@example.com\",\"password\":\"password\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("test-token"));

        verify(authService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    void login_InvalidCredentials_ReturnsErrorResponse() throws Exception {
        // Arrange
        when(authService.login(any(LoginRequest.class)))
            .thenThrow(new AuthenticationException("Incorrect username or password"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType("application/json")
                .content("{\"username\":\"test@example.com\",\"password\":\"wrongpassword\"}"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.path").value("/api/auth/login"))
            .andExpect(jsonPath("$.message").value("Authentication required: Incorrect username or password"))
            .andExpect(jsonPath("$.error").value("Unauthorized"))
            .andExpect(jsonPath("$.status").value(401));

        verify(authService, times(1)).login(any(LoginRequest.class));
    }
} 