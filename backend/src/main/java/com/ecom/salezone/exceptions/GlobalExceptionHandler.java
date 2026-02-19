package com.ecom.salezone.exceptions;

import com.ecom.salezone.dtos.ApiError;
import com.ecom.salezone.dtos.ApiResponseMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Global exception handler for the application.
 * Handles all custom and validation-related exceptions
 * and returns consistent error responses to the client.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    // Logger for centralized exception handling
    private static final Logger logger =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler({
            UsernameNotFoundException.class,
            BadCredentialsException.class,
            CredentialsExpiredException.class,
            DisabledException.class

    })
    public ResponseEntity<ApiError> handleAuthException(Exception e, HttpServletRequest request) {
        logger.info("Exception  : {}", e.getClass().getName());
        ApiError apiError = new ApiError();
        apiError.setStatus(HttpStatus.UNAUTHORIZED.value());
        apiError.setMessage(e.getMessage());
        apiError.setError("Bad Request");
        apiError.setPath(request.getRequestURI());
        apiError.setTimestamp(OffsetDateTime.now());
        return ResponseEntity.badRequest().body(apiError);

    }

    /**
     * Handles ResourceNotFoundException
     * Occurs when requested resource does not exist in DB
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseMessage> handleResourceNotFoundException(
            ResourceNotFoundException ex) {

        logger.error("Resource not found | message={}", ex.getMessage());

        ApiResponseMessage response = new ApiResponseMessage();
        response.setMessage(ex.getMessage());
        response.setSuccess(true); // keeping as per your existing logic
        response.setStatus(HttpStatus.NOT_FOUND);

        logger.info("Returning NOT_FOUND response | {}", response);

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles validation errors triggered by @Valid
     * Example: missing fields, invalid input values
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex) {

        logger.warn("Validation failed for request payload");

        List<ObjectError> allErrors =
                ex.getBindingResult().getAllErrors();

        Map<String, Object> response = new HashMap<>();

        // Collect field-wise validation messages
        allErrors.forEach(objectError -> {
            String message = objectError.getDefaultMessage();
            String field = ((FieldError) objectError).getField();
            response.put(field, message);

            logger.debug("Validation error | field={} message={}",
                    field, message);
        });

        logger.info("Returning BAD_REQUEST for validation errors | errorCount={}",
                response.size());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles custom BadApiRequestException
     * Used for business validation failures
     */
    @ExceptionHandler(BadApiRequestException.class)
    public ResponseEntity<ApiResponseMessage> handleBadApiRequest(
            BadApiRequestException ex) {

        logger.warn("Bad API request | message={}", ex.getMessage());

        ApiResponseMessage response =
                ApiResponseMessage.builder()
                        .message(ex.getMessage())
                        .status(HttpStatus.BAD_REQUEST)
                        .success(false)
                        .build();

        logger.info("Returning BAD_REQUEST response | {}", response);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
