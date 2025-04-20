package com.example.geosvc.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class DistanceRequest {
    @NotBlank(message = "First postcode is required")
    @Pattern(regexp = "^[A-Z]{1,2}[0-9][A-Z0-9]? ?[0-9][A-Z]{2}$", 
            message = "Invalid UK postcode format for first postcode")
    private String postcode1;

    @NotBlank(message = "Second postcode is required")
    @Pattern(regexp = "^[A-Z]{1,2}[0-9][A-Z0-9]? ?[0-9][A-Z]{2}$", 
            message = "Invalid UK postcode format for second postcode")
    private String postcode2;

    public DistanceRequest() {
    }

    public DistanceRequest(String postcode1, String postcode2) {
        this.postcode1 = postcode1;
        this.postcode2 = postcode2;
    }

    public String getPostcode1() {
        return postcode1;
    }

    public void setPostcode1(String postcode1) {
        this.postcode1 = postcode1;
    }

    public String getPostcode2() {
        return postcode2;
    }

    public void setPostcode2(String postcode2) {
        this.postcode2 = postcode2;
    }
} 