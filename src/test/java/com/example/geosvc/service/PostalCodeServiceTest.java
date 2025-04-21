package com.example.geosvc.service;

import com.example.geosvc.dto.DistanceResponse;
import com.example.geosvc.model.PostalCode;
import com.example.geosvc.repository.PostalCodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostalCodeServiceTest {

    @Mock
    private PostalCodeRepository postalCodeRepository;

    @InjectMocks
    private PostalCodeService postalCodeService;

    private PostalCode postalCode1;
    private PostalCode postalCode2;

    @BeforeEach
    void setUp() {
        postalCode1 = new PostalCode("SW1A 1AA", 
            new BigDecimal("51.5035"), 
            new BigDecimal("-0.1277"));
        
        postalCode2 = new PostalCode("EC2A 2AH", 
            new BigDecimal("51.5200"), 
            new BigDecimal("-0.0800"));
    }

    @Test
    void calculateDistance_ValidPostcodes_ReturnsCorrectDistance() {
        // Arrange
        when(postalCodeRepository.findByPostcode("SW1A 1AA"))
            .thenReturn(Optional.of(postalCode1));
        when(postalCodeRepository.findByPostcode("EC2A 2AH"))
            .thenReturn(Optional.of(postalCode2));

        // Act
        DistanceResponse response = postalCodeService.calculateDistance("SW1A 1AA", "EC2A 2AH");

        // Assert
        assertNotNull(response);
        assertEquals("SW1A 1AA", response.getLocation1().getPostcode());
        assertEquals("EC2A 2AH", response.getLocation2().getPostcode());
        assertEquals("km", response.getUnit());
        assertTrue(response.getDistance() > 0);
        
        verify(postalCodeRepository, times(1)).findByPostcode("SW1A 1AA");
        verify(postalCodeRepository, times(1)).findByPostcode("EC2A 2AH");
    }

    @Test
    void calculateDistance_InvalidPostcode_ThrowsException() {
        // Arrange
        when(postalCodeRepository.findByPostcode("INVALID"))
            .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            postalCodeService.calculateDistance("INVALID", "SW1A 1AA");
        });
        
        verify(postalCodeRepository, times(1)).findByPostcode("INVALID");
        verify(postalCodeRepository, never()).findByPostcode("SW1A 1AA");
    }

    @Test
    void createOrUpdatePostalCode_NewPostcode_CreatesNewRecord() {
        // Arrange
        PostalCode newPostalCode = new PostalCode("NW1 6XE", 
            new BigDecimal("51.5322"), 
            new BigDecimal("-0.1277"));
        
        when(postalCodeRepository.findByPostcode("NW1 6XE"))
            .thenReturn(Optional.empty());
        when(postalCodeRepository.save(any(PostalCode.class)))
            .thenReturn(newPostalCode);

        // Act
        PostalCode result = postalCodeService.createOrUpdatePostalCode(newPostalCode);

        // Assert
        assertNotNull(result);
        assertEquals("NW1 6XE", result.getPostcode());
        verify(postalCodeRepository, times(1)).findByPostcode("NW1 6XE");
        verify(postalCodeRepository, times(1)).save(newPostalCode);
    }

    @Test
    void createOrUpdatePostalCode_ExistingPostcode_UpdatesRecord() {
        // Arrange
        PostalCode updatedPostalCode = new PostalCode("SW1A 1AA", 
            new BigDecimal("51.5036"), 
            new BigDecimal("-0.1278"));
        
        when(postalCodeRepository.findByPostcode("SW1A 1AA"))
            .thenReturn(Optional.of(postalCode1));
        when(postalCodeRepository.save(any(PostalCode.class)))
            .thenReturn(updatedPostalCode);

        // Act
        PostalCode result = postalCodeService.createOrUpdatePostalCode(updatedPostalCode);

        // Assert
        assertNotNull(result);
        assertEquals("SW1A 1AA", result.getPostcode());
        assertEquals(new BigDecimal("51.5036"), result.getLatitude());
        assertEquals(new BigDecimal("-0.1278"), result.getLongitude());
        verify(postalCodeRepository, times(1)).findByPostcode("SW1A 1AA");
        verify(postalCodeRepository, times(1)).save(postalCode1);
    }
} 