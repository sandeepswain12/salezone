package com.ecom.salezone.exceptions;

import com.ecom.salezone.dtos.ApiError;
import com.ecom.salezone.dtos.ApiResponseMessage;
import com.ecom.salezone.util.LogKeyGenerator;
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

    private static final Logger logger =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({
            UsernameNotFoundException.class,
            BadCredentialsException.class,
            CredentialsExpiredException.class,
            DisabledException.class
    })
    public ResponseEntity<ApiError> handleAuthException(
            Exception e,
            HttpServletRequest request) {

        String logKey = LogKeyGenerator.generateLogKey();

        logger.error("LogKey: {} - Authentication exception | type={} path={} message={}",
                logKey, e.getClass().getSimpleName(),
                request.getRequestURI(),
                e.getMessage());

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
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        String logKey = LogKeyGenerator.generateLogKey();

        logger.error("LogKey: {} - Resource not found | path={} message={}",
                logKey, request.getRequestURI(), ex.getMessage());

        ApiResponseMessage response = new ApiResponseMessage();
        response.setMessage(ex.getMessage());
        response.setSuccess(true);
        response.setStatus(HttpStatus.NOT_FOUND);

        logger.info("LogKey: {} - Returning NOT_FOUND response", logKey);

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles validation errors triggered by @Valid
     * Example: missing fields, invalid input values
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String logKey = LogKeyGenerator.generateLogKey();

        logger.warn("LogKey: {} - Validation failed | path={}",
                logKey, request.getRequestURI());

        List<ObjectError> allErrors =
                ex.getBindingResult().getAllErrors();

        Map<String, Object> response = new HashMap<>();

        allErrors.forEach(objectError -> {
            String message = objectError.getDefaultMessage();
            String field = ((FieldError) objectError).getField();
            response.put(field, message);

            logger.debug("LogKey: {} - Validation error | field={} message={}",
                    logKey, field, message);
        });

        logger.info("LogKey: {} - Returning BAD_REQUEST | errorCount={}",
                logKey, response.size());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles custom BadApiRequestException
     * Used for business validation failures
     */
    @ExceptionHandler(BadApiRequestException.class)
    public ResponseEntity<ApiResponseMessage> handleBadApiRequest(
            BadApiRequestException ex,
            HttpServletRequest request) {

        String logKey = LogKeyGenerator.generateLogKey();

        logger.warn("LogKey: {} - Bad API request | path={} message={}",
                logKey, request.getRequestURI(), ex.getMessage());

        ApiResponseMessage response =
                ApiResponseMessage.builder()
                        .message(ex.getMessage())
                        .status(HttpStatus.BAD_REQUEST)
                        .success(false)
                        .build();

        logger.info("LogKey: {} - Returning BAD_REQUEST response", logKey);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}