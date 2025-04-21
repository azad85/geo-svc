package com.example.geosvc.aspect;

import com.example.geosvc.dto.DistanceRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Aspect
public class TestPostalCodeLoggingAspect {
    private final Logger logger;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final ThreadLocal<String> requestIdHolder = new ThreadLocal<>();
    private static final String EVENT_TYPE = "get_postalcode_latlong_distance";

    public TestPostalCodeLoggingAspect(Logger logger) {
        this.logger = logger;
    }

    @Before("execution(* com.example.geosvc.aspect.PostalCodeLoggingAspectTest.TestService.calculateDistance(..)) && args(request)")
    public void logDistanceRequest(DistanceRequest request) {
        String requestId = UUID.randomUUID().toString();
        requestIdHolder.set(requestId);
        
        String timestamp = LocalDateTime.now().format(formatter);
        String logMessage = String.format("POSTAL_CODE_REQUEST|%s|%s|%s|%s|%s|%s",
            timestamp,
            requestId,
            EVENT_TYPE,
            request.getPostcode1(),
            request.getPostcode2(),
            "REQUEST_RECEIVED");
        
        logger.info(logMessage);
    }

    @AfterReturning(pointcut = "execution(* com.example.geosvc.aspect.PostalCodeLoggingAspectTest.TestService.calculateDistance(..))", 
                   returning = "result")
    public void logDistanceResponse(JoinPoint joinPoint, Object result) {
        DistanceRequest request = (DistanceRequest) joinPoint.getArgs()[0];
        String requestId = requestIdHolder.get();
        
        String timestamp = LocalDateTime.now().format(formatter);
        String logMessage = String.format("POSTAL_CODE_REQUEST|%s|%s|%s|%s|%s|%s",
            timestamp,
            requestId,
            EVENT_TYPE,
            request.getPostcode1(),
            request.getPostcode2(),
            "REQUEST_COMPLETED");
        
        logger.info(logMessage);
        requestIdHolder.remove();
    }
} 