package com.ecom.salezone.exceptions;

import lombok.Builder;

/**
 * Custom exception thrown when a requested resource
 * is not found in the database.
 *
 * This exception is handled globally by GlobalExceptionHandler
 * and returns a HTTP 404 (NOT_FOUND) response.
 *
 * @author : Sandeep Kumar Swain
 * @version : 1.0
 * @since : 15-03-2026
 */
@Builder
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException() {
        super("Resource Not Found !!");
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
