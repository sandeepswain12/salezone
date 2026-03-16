package com.ecom.salezone.controller;

import com.ecom.salezone.services.CloudnaryImageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * CloudnaryImageController handles image upload operations
 * for the SaleZone E-commerce system.
 *
 * This controller provides APIs for:
 * - Uploading images to Cloudinary cloud storage
 * - Returning the public URL of uploaded images
 *
 * Features:
 * - Secure image upload
 * - Cloud storage using Cloudinary
 * - Public URL generation for images
 *
 * Typical Usage:
 * - Upload product images
 * - Upload user profile images
 * - Store images outside application server
 *
 * @author : Sandeep Kumar Swain
 * @version : 1.0
 * @since : 15-03-2026
 */

@Tag(
        name = "Image Upload APIs",
        description = "APIs for uploading and managing images using Cloudinary"
)
@RestController
@RequestMapping("/images")
public class CloudnaryImageController {

    private final CloudnaryImageService cloudinaryService;

    public CloudnaryImageController(CloudnaryImageService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    @Operation(
            summary = "Upload image",
            description = "Uploads an image file to Cloudinary and returns the image URL."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid image file"),
            @ApiResponse(responseCode = "500", description = "Image upload failed")
    })
    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(
            @RequestParam("image") MultipartFile file
    ) {

        String imageUrl = cloudinaryService.uploadImage(file, "dfd");

        return ResponseEntity.ok(imageUrl);
    }
}