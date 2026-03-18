package com.ecom.salezone.exceptions;

/**
 * Custom exception thrown when a client sends
 * an invalid or malformed API request.
 *
 * This is typically used for business validation failures
 * such as invalid input values or incorrect request data.
 *
 * The exception is handled by {@link GlobalExceptionHandler}
 * and returns an HTTP 400 (BAD_REQUEST) response.
 *
 * @author : Sandeep Kumar Swain
 * @version : 1.0
 * @since : 15-03-2026
 */
public class BadApiRequestException extends RuntimeException {

    public BadApiRequestException() {
        super("Bad Request !!");
    }

    public BadApiRequestException(String message) {
        super(message);
    }
}