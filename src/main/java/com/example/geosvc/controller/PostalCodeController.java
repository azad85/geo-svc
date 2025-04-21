package com.example.geosvc.controller;

import com.example.geosvc.dto.DistanceRequest;
import com.example.geosvc.dto.DistanceResponse;
import com.example.geosvc.dto.PostalCodeResponse;
import com.example.geosvc.dto.UpdatePostalCodeRequest;
import com.example.geosvc.model.PostalCode;
import com.example.geosvc.service.PostalCodeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @GetMapping("/{postcode}")
    public ResponseEntity<PostalCodeResponse> getPostalCodeMapping(@PathVariable String postcode) {
        PostalCodeResponse response = postalCodeService.getPostalCodeMapping(postcode);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{postcode}")
    public ResponseEntity<PostalCodeResponse> updatePostalCodeMapping(
            @PathVariable String postcode,
            @Valid @RequestBody UpdatePostalCodeRequest request) {
        PostalCodeResponse response = postalCodeService.updatePostalCodeMapping(postcode, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<PostalCodeResponse>> getAllPostalCodes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "postcode") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<PostalCodeResponse> response = postalCodeService.getAllPostalCodes(pageable);
        return ResponseEntity.ok(response);
    }
} 