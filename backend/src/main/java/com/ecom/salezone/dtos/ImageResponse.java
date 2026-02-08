package com.ecom.salezone.dtos;

import lombok.*;
import org.springframework.http.HttpStatus;

/**
 * Response DTO used for image upload operations.
 * Sent after successfully uploading or updating an image.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageResponse {

    /**
     * Name of the image file stored on the server.
     * This is typically used by the frontend to fetch or display the image.
     */
    private String imageName;

    /**
     * Human-readable message describing the result of the image operation.
     * Example: "Image uploaded successfully"
     */
    private String message;

    /**
     * Indicates whether the image operation was successful or not.
     * true  → upload/update successful
     * false → operation failed
     */
    private boolean success;

    /**
     * HTTP status associated with the image upload response.
     * Example: CREATED, OK, BAD_REQUEST
     */
    private HttpStatus status;
}
