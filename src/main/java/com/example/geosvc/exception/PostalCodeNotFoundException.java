package com.example.geosvc.exception;

public class PostalCodeNotFoundException extends RuntimeException {
    public PostalCodeNotFoundException(String postcode) {
        super("Postal code not found: " + postcode);
    }
} 