package com.example.geosvc.service;

import com.example.geosvc.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public Map<String, String> authenticateAndGenerateToken(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, password)
        );
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);
        
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return response;
    }
    
    public static class AuthenticationResponse {
        private final boolean success;
        private final Map<String, Object> data;
        private final Map<String, Object> error;

        private AuthenticationResponse(boolean success, Map<String, Object> data, Map<String, Object> error) {
            this.success = success;
            this.data = data;
            this.error = error;
        }

        public boolean isSuccess() {
            return success;
        }

        public Map<String, Object> getData() {
            return data;
        }

        public Map<String, Object> getError() {
            return error;
        }

        public static AuthenticationResponse success(Map<String, String> data) {
            return new AuthenticationResponse(true, new HashMap<>(data), null);
        }

        public static AuthenticationResponse error(String message) {
            Map<String, Object> errorMap = new HashMap<>();
            errorMap.put("status", HttpStatus.UNAUTHORIZED.value());
            errorMap.put("error", "Unauthorized");
            errorMap.put("message", message);
            return new AuthenticationResponse(false, null, errorMap);
        }
    }

    public AuthenticationResponse login(String username, String password) {
        try {
            Map<String, String> tokenResponse = authenticateAndGenerateToken(username, password);
            return AuthenticationResponse.success(tokenResponse);
        } catch (BadCredentialsException e) {
            return AuthenticationResponse.error("Invalid username or password");
        } catch (AuthenticationException e) {
            return AuthenticationResponse.error(e.getMessage());
        }
    }
} 