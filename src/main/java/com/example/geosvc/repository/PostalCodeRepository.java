package com.example.geosvc.repository;

import com.example.geosvc.model.PostalCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostalCodeRepository extends JpaRepository<PostalCode, Long> {
    Optional<PostalCode> findByPostcode(String postcode);
} 