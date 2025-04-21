package com.example.geosvc.controller;

import com.example.geosvc.dto.PostalCodeResponse;
import com.example.geosvc.dto.UpdatePostalCodeRequest;
import com.example.geosvc.exception.GlobalExceptionHandler;
import com.example.geosvc.exception.PostalCodeNotFoundException;
import com.example.geosvc.service.PostalCodeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PostalCodeControllerTest {

    @Mock
    private PostalCodeService postalCodeService;

    @InjectMocks
    private PostalCodeController postalCodeController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(postalCodeController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getPostalCodeMapping_ValidPostcode_ReturnsMapping() throws Exception {
        // Arrange
        PostalCodeResponse response = new PostalCodeResponse(
            "SW1A 1AA",
            new BigDecimal("51.5035"),
            new BigDecimal("-0.1277")
        );
        when(postalCodeService.getPostalCodeMapping(any())).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/postal-codes/SW1A 1AA"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.postcode").value("SW1A 1AA"))
            .andExpect(jsonPath("$.latitude").value(51.5035))
            .andExpect(jsonPath("$.longitude").value(-0.1277));

        verify(postalCodeService, times(1)).getPostalCodeMapping("SW1A 1AA");
    }

    @Test
    void getPostalCodeMapping_InvalidPostcode_ReturnsNotFound() throws Exception {
        // Arrange
        when(postalCodeService.getPostalCodeMapping(any()))
            .thenThrow(new PostalCodeNotFoundException("INVALID"));

        // Act & Assert
        mockMvc.perform(get("/api/postal-codes/INVALID"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.path").value("/api/postal-codes/INVALID"))
            .andExpect(jsonPath("$.message").value("Postal code not found: INVALID"))
            .andExpect(jsonPath("$.error").value("Not Found"))
            .andExpect(jsonPath("$.status").value(404));

        verify(postalCodeService, times(1)).getPostalCodeMapping("INVALID");
    }

    @Test
    void updatePostalCodeMapping_ValidPostcode_ReturnsUpdatedMapping() throws Exception {
        // Arrange
        String postcode = "SW1A 1AA";
        UpdatePostalCodeRequest request = new UpdatePostalCodeRequest(
                new BigDecimal("51.5036"),
                new BigDecimal("-0.1278")
        );
        
        PostalCodeResponse response = new PostalCodeResponse(
                postcode,
                request.getLatitude(),
                request.getLongitude()
        );
        
        when(postalCodeService.updatePostalCodeMapping(eq(postcode), any(UpdatePostalCodeRequest.class)))
                .thenReturn(response);
        
        // Act & Assert
        mockMvc.perform(put("/api/postal-codes/{postcode}", postcode)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postcode").value(postcode))
                .andExpect(jsonPath("$.latitude").value(51.5036))
                .andExpect(jsonPath("$.longitude").value(-0.1278));
        
        verify(postalCodeService, times(1))
                .updatePostalCodeMapping(eq(postcode), any(UpdatePostalCodeRequest.class));
    }
    
    @Test
    void updatePostalCodeMapping_InvalidPostcode_ReturnsNotFound() throws Exception {
        // Arrange
        String postcode = "INVALID";
        UpdatePostalCodeRequest request = new UpdatePostalCodeRequest(
                new BigDecimal("51.5036"),
                new BigDecimal("-0.1278")
        );
        
        when(postalCodeService.updatePostalCodeMapping(eq(postcode), any(UpdatePostalCodeRequest.class)))
                .thenThrow(new PostalCodeNotFoundException(postcode));
        
        // Act & Assert
        mockMvc.perform(put("/api/postal-codes/{postcode}", postcode)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Postal code not found: " + postcode));
        
        verify(postalCodeService, times(1))
                .updatePostalCodeMapping(eq(postcode), any(UpdatePostalCodeRequest.class));
    }

    @Test
    void getAllPostalCodes_ReturnsPaginatedResponse() throws Exception {
        // Arrange
        int page = 0;
        int size = 10;
        String sortBy = "postcode";
        
        PostalCodeResponse response1 = new PostalCodeResponse(
                "SW1A 1AA",
                new BigDecimal("51.5035"),
                new BigDecimal("-0.1277")
        );
        
        PostalCodeResponse response2 = new PostalCodeResponse(
                "EC2A 2AH",
                new BigDecimal("51.5200"),
                new BigDecimal("-0.0800")
        );
        
        List<PostalCodeResponse> content = Arrays.asList(response1, response2);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<PostalCodeResponse> responsePage = new PageImpl<>(content, pageable, content.size());
        
        when(postalCodeService.getAllPostalCodes(any(Pageable.class))).thenReturn(responsePage);
        
        // Act & Assert
        mockMvc.perform(get("/api/postal-codes")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .param("sortBy", sortBy))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].postcode").value("SW1A 1AA"))
                .andExpect(jsonPath("$.content[1].postcode").value("EC2A 2AH"))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.size").value(size))
                .andExpect(jsonPath("$.number").value(page));
        
        verify(postalCodeService, times(1)).getAllPostalCodes(any(Pageable.class));
    }
} 