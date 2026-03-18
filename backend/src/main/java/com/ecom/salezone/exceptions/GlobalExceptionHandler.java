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
 * Global exception handler for the SaleZone application.
 *
 * This class centralizes exception handling across all controllers
 * using Spring's @ControllerAdvice mechanism.
 *
 * Responsibilities:
 * - Handle authentication related exceptions
 * - Handle validation errors (@Valid failures)
 * - Handle custom business exceptions
 * - Handle database constraint violations
 * - Provide consistent error responses to clients
 *
 * Each exception handler logs the error with a unique logKey
 * for better request tracing and debugging.
 *
 * Standardized error responses include:
 * - HTTP status code
 * - error message
 * - request path
 * - timestamp
 *
 * @author : Sandeep Kumar Swain
 * @version : 1.0
 * @since : 15-03-2026
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles authentication related exceptions such as:
     * - UsernameNotFoundException
     * - BadCredentialsException
     * - CredentialsExpiredException
     * - DisabledException
     *
     * Returns a standardized UNAUTHORIZED error response.
     *
     * @param e authentication exception
     * @param request current HTTP request
     * @return ApiError response with HTTP 401 status
     */
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
     * Handles ResourceNotFoundException.
     *
     * This exception occurs when a requested resource
     * is not found in the database.
     *
     * @param ex thrown exception
     * @param request HTTP request
     * @return ApiResponseMessage with HTTP 404 status
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
     * Handles validation errors triggered by @Valid annotations.
     *
     * Extracts field-level validation errors and returns them
     * as a map of field -> error message.
     *
     * Example:
     * {
     *   "email": "Email is required",
     *   "password": "Password must be at least 8 characters"
     * }
     *
     * @param ex validation exception
     * @param request HTTP request
     * @return map containing validation errors with HTTP 400 status
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
     * Handles custom BadApiRequestException.
     *
     * Used for business rule violations such as:
     * - invalid operations
     * - incorrect input values
     * - logical errors in requests
     *
     * @param ex thrown exception
     * @param request HTTP request
     * @return ApiResponseMessage with HTTP 400 status
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

    /**
     * Handles database constraint violations.
     *
     * Example scenarios:
     * - duplicate email
     * - unique constraint violations
     * - foreign key constraint errors
     *
     * Provides user-friendly error messages instead of
     * exposing raw database errors.
     *
     * @param ex database exception
     * @param request HTTP request
     * @return ApiResponseMessage with HTTP 400 status
     */
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponseMessage> handleDataIntegrityViolation(
            org.springframework.dao.DataIntegrityViolationException ex,
            HttpServletRequest request) {

        String logKey = LogKeyGenerator.generateLogKey();

        logger.error("LogKey: {} - Data integrity violation | path={} message={}",
                logKey, request.getRequestURI(), ex.getMessage());

        String message = "Database constraint violation";

        // 🔥 Detect duplicate email specifically
        if (ex.getRootCause() != null &&
                ex.getRootCause().getMessage().contains("users.UK")) {

            message = "Email already exists. Please use another email.";
        }

        ApiResponseMessage response =
                ApiResponseMessage.builder()
                        .message(message)
                        .status(HttpStatus.BAD_REQUEST)
                        .success(false)
                        .build();

        logger.info("LogKey: {} - Returning BAD_REQUEST response for DB violation", logKey);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}