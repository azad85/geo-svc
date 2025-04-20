package com.example.geosvc.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                        AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        
        String errorMessage = determineErrorMessage(request, authException);
        errorDetails.put("message", errorMessage);
        errorDetails.put("error", "Unauthorized");
        errorDetails.put("path", request.getServletPath());
        
        response.getWriter().write(objectMapper.writeValueAsString(errorDetails));
    }
    
    private String determineErrorMessage(HttpServletRequest request, AuthenticationException authException) {
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null) {
            return "Authentication required: Missing Authorization header";
        } else if (!authHeader.startsWith("Bearer ")) {
            return "Authentication required: Invalid Authorization header format";
        } else {
            return authException.getMessage() != null ? 
                   authException.getMessage() : "Authentication failed: Invalid or expired token";
        }
    }
} 