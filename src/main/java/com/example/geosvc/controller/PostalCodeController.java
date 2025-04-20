package com.example.geosvc.controller;

import com.example.geosvc.dto.DistanceRequest;
import com.example.geosvc.dto.DistanceResponse;
import com.example.geosvc.model.PostalCode;
import com.example.geosvc.service.PostalCodeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/postal-codes")
public class PostalCodeController {
    private final PostalCodeService postalCodeService;

    public PostalCodeController(PostalCodeService postalCodeService) {
        this.postalCodeService = postalCodeService;
    }

    @PostMapping("/distance")
    public ResponseEntity<DistanceResponse> calculateDistance(@Valid @RequestBody DistanceRequest request) {
        return ResponseEntity.ok(postalCodeService.calculateDistance(request.getPostcode1(), request.getPostcode2()));
    }

    @PostMapping
    public ResponseEntity<PostalCode> createOrUpdatePostalCode(@Valid @RequestBody PostalCode postalCode) {
        return ResponseEntity.ok(postalCodeService.createOrUpdatePostalCode(postalCode));
    }
} 