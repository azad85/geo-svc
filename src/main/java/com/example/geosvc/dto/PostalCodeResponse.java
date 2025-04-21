package com.example.geosvc.dto;

import java.math.BigDecimal;

public class PostalCodeResponse {
    private String postcode;
    private BigDecimal latitude;
    private BigDecimal longitude;

    public PostalCodeResponse() {
    }

    public PostalCodeResponse(String postcode, BigDecimal latitude, BigDecimal longitude) {
        this.postcode = postcode;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }
} 