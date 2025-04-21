package com.example.geosvc.aspect;

import com.example.geosvc.dto.DistanceRequest;
import com.example.geosvc.dto.DistanceResponse;
import com.example.geosvc.model.PostalCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostalCodeLoggingAspectTest {

    @Mock
    private Logger logger;

    @Captor
    private ArgumentCaptor<String> logMessageCaptor;

    private TestPostalCodeLoggingAspect aspect;
    private TestService testService;

    @BeforeEach
    void setUp() {
        aspect = new TestPostalCodeLoggingAspect(logger);
        testService = new TestService();
        
        // Create a proxy of the test service with our aspect
        AspectJProxyFactory factory = new AspectJProxyFactory(testService);
        factory.addAspect(aspect);
        testService = factory.getProxy();
    }

    @Test
    void logDistanceRequest_ValidRequest_LogsCorrectly() {
        // Arrange
        DistanceRequest request = new DistanceRequest("SW1A 1AA", "EC2A 2AH");

        // Act
        testService.calculateDistance(request);

        // Assert
        verify(logger, times(2)).info(logMessageCaptor.capture());
        
        String requestLog = logMessageCaptor.getAllValues().get(0);
        String responseLog = logMessageCaptor.getAllValues().get(1);
        
        assertTrue(requestLog.startsWith("POSTAL_CODE_REQUEST|"));
        assertTrue(requestLog.contains("SW1A 1AA"));
        assertTrue(requestLog.contains("EC2A 2AH"));
        assertTrue(requestLog.contains("REQUEST_RECEIVED"));
        
        assertTrue(responseLog.startsWith("POSTAL_CODE_REQUEST|"));
        assertTrue(responseLog.contains("SW1A 1AA"));
        assertTrue(responseLog.contains("EC2A 2AH"));
        assertTrue(responseLog.contains("REQUEST_COMPLETED"));
    }

    // Test service to demonstrate the aspect
    public static class TestService {
        public TestService() {
        }

        public DistanceResponse calculateDistance(DistanceRequest request) {
            // Simulate some processing
            return new DistanceResponse(
                new DistanceResponse.Location("SW1A 1AA", 51.5035, -0.1277),
                new DistanceResponse.Location("EC2A 2AH", 51.5200, -0.0800),
                3.45
            );
        }
    }
} 