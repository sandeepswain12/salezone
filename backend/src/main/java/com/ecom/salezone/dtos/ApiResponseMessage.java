package com.ecom.salezone.dtos;

import lombok.*;
import org.springframework.http.HttpStatus;

/**
 * Generic response DTO used to send API operation results to the client.
 * Commonly used for success or failure messages (create, update, delete).
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ApiResponseMessage {

    /**
     * Human-readable message describing the result of the API operation.
     * Example: "Product created successfully", "Order is deleted"
     */
    private String message;

    /**
     * Indicates whether the API operation was successful or not.
     * true  → operation completed successfully
     * false → operation failed due to validation or business error
     */
    private boolean success;

    /**
     * HTTP status associated with the API response.
     * Example: OK, CREATED, BAD_REQUEST, NOT_FOUND
     */
    private HttpStatus status;
}
