package com.e3.api.product_device.filter;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoggingFilter extends OncePerRequestFilter {
    public static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request, 
        @NonNull HttpServletResponse response, 
        @NonNull FilterChain filterChain)
            throws ServletException, IOException {
                
        String correlationId = UUID.randomUUID().toString();
        logRequest(request, correlationId);
        ResponseWrapper responseWrapper = new ResponseWrapper(response);

        try {
            filterChain.doFilter(request, responseWrapper);
        } finally {
            logResponse(responseWrapper, correlationId);
        }
    }

    private void logRequest(HttpServletRequest request, String correlationId) {
        logger.info("Request [{}]: {} {}", correlationId, request.getMethod(), request.getRequestURI());
    }

    private void logResponse(ResponseWrapper responseWrapper, String correlationId) {
        logger.info("Response [{}]: Status = {}, Body = {}", correlationId, 
            responseWrapper.getStatus(), 
            new String(responseWrapper.getContentAsByteArray()));
    }

}
