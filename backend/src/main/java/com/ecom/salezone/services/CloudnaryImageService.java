package com.ecom.salezone.services;

import org.springframework.web.multipart.MultipartFile;

/**
 * CloudnaryImageService defines operations related to
 * image upload and management using Cloudinary.
 *
 * Responsibilities:
 * - Upload images to Cloudinary cloud storage
 * - Return the public URL of the uploaded image
 *
 * Images uploaded using this service can be used for:
 * - Product images
 * - User profile images
 * - Other media assets in the SaleZone system
 *
 * @author : Sandeep Kumar Swain
 * @version : 1.0
 * @since : 15-03-2026
 */
public interface CloudnaryImageService {

    /**
     * Uploads an image file to Cloudinary.
     *
     * @param file   image file to upload
     * @param logKey unique request identifier used for logging and tracing
     *
     * @return public URL of the uploaded image
     */
    String uploadImage(MultipartFile file, String logKey);
}