package com.example.geosvc.service;

import com.example.geosvc.dto.DistanceResponse;
import com.example.geosvc.dto.PostalCodeResponse;
import com.example.geosvc.dto.UpdatePostalCodeRequest;
import com.example.geosvc.exception.PostalCodeNotFoundException;
import com.example.geosvc.model.PostalCode;
import com.example.geosvc.repository.PostalCodeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Service
public class PostalCodeService {
    private final PostalCodeRepository postalCodeRepository;

    public PostalCodeService(PostalCodeRepository postalCodeRepository) {
        this.postalCodeRepository = postalCodeRepository;
    }

    public DistanceResponse calculateDistance(String postcode1, String postcode2) {
        PostalCode pc1 = postalCodeRepository.findByPostcode(postcode1)
                .orElseThrow(() -> new IllegalArgumentException("Postcode not found: " + postcode1));
        PostalCode pc2 = postalCodeRepository.findByPostcode(postcode2)
                .orElseThrow(() -> new IllegalArgumentException("Postcode not found: " + postcode2));

        double distance = calculateHaversineDistance(
                pc1.getLatitude().doubleValue(), pc1.getLongitude().doubleValue(),
                pc2.getLatitude().doubleValue(), pc2.getLongitude().doubleValue());

        DistanceResponse.Location location1 = new DistanceResponse.Location(
                pc1.getPostcode(), 
                pc1.getLatitude().doubleValue(), 
                pc1.getLongitude().doubleValue());
        DistanceResponse.Location location2 = new DistanceResponse.Location(
                pc2.getPostcode(), 
                pc2.getLatitude().doubleValue(), 
                pc2.getLongitude().doubleValue());

        return new DistanceResponse(location1, location2, distance);
    }

    @Transactional
    public PostalCode createOrUpdatePostalCode(PostalCode postalCode) {
        return postalCodeRepository.findByPostcode(postalCode.getPostcode())
                .map(existing -> {
                    existing.setLatitude(postalCode.getLatitude());
                    existing.setLongitude(postalCode.getLongitude());
                    return postalCodeRepository.save(existing);
                })
                .orElseGet(() -> postalCodeRepository.save(postalCode));
    }

    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    public PostalCodeResponse getPostalCodeMapping(String postcode) {
        Optional<PostalCode> postalCode = postalCodeRepository.findByPostcode(postcode);
        return postalCode.map(pc -> new PostalCodeResponse(pc.getPostcode(), pc.getLatitude(), pc.getLongitude()))
            .orElseThrow(() -> new PostalCodeNotFoundException(postcode));
    }

    @Transactional
    public PostalCodeResponse updatePostalCodeMapping(String postcode, UpdatePostalCodeRequest request) {
        PostalCode postalCode = postalCodeRepository.findByPostcode(postcode)
                .orElseThrow(() -> new PostalCodeNotFoundException(postcode));

        postalCode.setLatitude(request.getLatitude());
        postalCode.setLongitude(request.getLongitude());
        
        PostalCode updatedPostalCode = postalCodeRepository.save(postalCode);
        
        return new PostalCodeResponse(
                updatedPostalCode.getPostcode(),
                updatedPostalCode.getLatitude(),
                updatedPostalCode.getLongitude()
        );
    }

    @Transactional(readOnly = true)
    public Page<PostalCodeResponse> getAllPostalCodes(Pageable pageable) {
        return postalCodeRepository.findAll(pageable)
                .map(postalCode -> new PostalCodeResponse(
                        postalCode.getPostcode(),
                        postalCode.getLatitude(),
                        postalCode.getLongitude()
                ));
    }
} 