package com.example.geosvc.service;

import com.example.geosvc.dto.DistanceResponse;
import com.example.geosvc.dto.PostalCodeResponse;
import com.example.geosvc.dto.UpdatePostalCodeRequest;
import com.example.geosvc.exception.PostalCodeNotFoundException;
import com.example.geosvc.model.PostalCode;
import com.example.geosvc.repository.PostalCodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
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
    private PostalCode postalCode;

    @BeforeEach
    void setUp() {
        postalCode1 = new PostalCode("SW1A 1AA", 
            new BigDecimal("51.5035"), 
            new BigDecimal("-0.1277"));
        
        postalCode2 = new PostalCode("EC2A 2AH", 
            new BigDecimal("51.5200"), 
            new BigDecimal("-0.0800"));

        postalCode = new PostalCode("SW1A 1AA", 
            new BigDecimal("51.5035"), 
            new BigDecimal("-0.1277"));
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

    @Test
    void getPostalCodeMapping_ValidPostcode_ReturnsMapping() {
        // Arrange
        when(postalCodeRepository.findByPostcode("SW1A 1AA"))
            .thenReturn(Optional.of(postalCode));

        // Act
        PostalCodeResponse response = postalCodeService.getPostalCodeMapping("SW1A 1AA");

        // Assert
        assertNotNull(response);
        assertEquals("SW1A 1AA", response.getPostcode());
        assertEquals(new BigDecimal("51.5035"), response.getLatitude());
        assertEquals(new BigDecimal("-0.1277"), response.getLongitude());
        verify(postalCodeRepository, times(1)).findByPostcode("SW1A 1AA");
    }

    @Test
    void getPostalCodeMapping_InvalidPostcode_ThrowsException() {
        // Arrange
        when(postalCodeRepository.findByPostcode("INVALID"))
            .thenReturn(Optional.empty());

        // Act & Assert
        PostalCodeNotFoundException exception = assertThrows(PostalCodeNotFoundException.class, () -> {
            postalCodeService.getPostalCodeMapping("INVALID");
        });
        
        assertEquals("Postal code not found: INVALID", exception.getMessage());
        verify(postalCodeRepository, times(1)).findByPostcode("INVALID");
    }

    @Test
    void updatePostalCodeMapping_ValidPostcode_ReturnsUpdatedMapping() {
        // Arrange
        String postcode = "SW1A 1AA";
        UpdatePostalCodeRequest request = new UpdatePostalCodeRequest(
                new BigDecimal("51.5036"),
                new BigDecimal("-0.1278")
        );
        
        PostalCode existingPostalCode = new PostalCode();
        existingPostalCode.setPostcode(postcode);
        existingPostalCode.setLatitude(new BigDecimal("51.5035"));
        existingPostalCode.setLongitude(new BigDecimal("-0.1277"));
        
        PostalCode updatedPostalCode = new PostalCode();
        updatedPostalCode.setPostcode(postcode);
        updatedPostalCode.setLatitude(request.getLatitude());
        updatedPostalCode.setLongitude(request.getLongitude());
        
        when(postalCodeRepository.findByPostcode(postcode))
                .thenReturn(Optional.of(existingPostalCode));
        when(postalCodeRepository.save(any(PostalCode.class)))
                .thenReturn(updatedPostalCode);
        
        // Act
        PostalCodeResponse response = postalCodeService.updatePostalCodeMapping(postcode, request);
        
        // Assert
        assertNotNull(response);
        assertEquals(postcode, response.getPostcode());
        assertEquals(request.getLatitude(), response.getLatitude());
        assertEquals(request.getLongitude(), response.getLongitude());
        
        verify(postalCodeRepository, times(1)).findByPostcode(postcode);
        verify(postalCodeRepository, times(1)).save(any(PostalCode.class));
    }
    
    @Test
    void updatePostalCodeMapping_InvalidPostcode_ThrowsException() {
        // Arrange
        String postcode = "INVALID";
        UpdatePostalCodeRequest request = new UpdatePostalCodeRequest(
                new BigDecimal("51.5036"),
                new BigDecimal("-0.1278")
        );
        
        when(postalCodeRepository.findByPostcode(postcode))
                .thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(PostalCodeNotFoundException.class, () -> {
            postalCodeService.updatePostalCodeMapping(postcode, request);
        });
        
        verify(postalCodeRepository, times(1)).findByPostcode(postcode);
        verify(postalCodeRepository, never()).save(any(PostalCode.class));
    }

    @Test
    void getAllPostalCodes_ReturnsPaginatedResponse() {
        // Arrange
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        
        PostalCode postalCode1 = new PostalCode();
        postalCode1.setPostcode("SW1A 1AA");
        postalCode1.setLatitude(new BigDecimal("51.5035"));
        postalCode1.setLongitude(new BigDecimal("-0.1277"));
        
        PostalCode postalCode2 = new PostalCode();
        postalCode2.setPostcode("EC2A 2AH");
        postalCode2.setLatitude(new BigDecimal("51.5200"));
        postalCode2.setLongitude(new BigDecimal("-0.0800"));
        
        List<PostalCode> postalCodes = Arrays.asList(postalCode1, postalCode2);
        Page<PostalCode> postalCodePage = new PageImpl<>(postalCodes, pageable, postalCodes.size());
        
        when(postalCodeRepository.findAll(pageable)).thenReturn(postalCodePage);
        
        // Act
        Page<PostalCodeResponse> response = postalCodeService.getAllPostalCodes(pageable);
        
        // Assert
        assertNotNull(response);
        assertEquals(2, response.getContent().size());
        assertEquals(postalCode1.getPostcode(), response.getContent().get(0).getPostcode());
        assertEquals(postalCode2.getPostcode(), response.getContent().get(1).getPostcode());
        
        verify(postalCodeRepository, times(1)).findAll(pageable);
    }
} 