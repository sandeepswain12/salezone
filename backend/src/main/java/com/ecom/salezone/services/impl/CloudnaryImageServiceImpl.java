package com.ecom.salezone.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ecom.salezone.services.CloudnaryImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Implementation of CloudnaryImageService for the SaleZone E-commerce system.
 *
 * Handles uploading product or user images to Cloudinary cloud storage
 * and returns the secure URL of the uploaded image.
 *
 * Uses Cloudinary Java SDK for file upload operations.
 *
 * @author Sandeep Kumar Swain
 * @version 1.0
 * @since 15-03-2026
 */
@Service
public class CloudnaryImageServiceImpl implements CloudnaryImageService {

    @Autowired
    private Cloudinary cloudinary;


    @Override
    public String uploadImage(MultipartFile file, String logKey) {
        try {

            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.emptyMap()
            );

            return uploadResult.get("secure_url").toString();

        } catch (Exception e) {
            e.printStackTrace();   // show real error in logs
            throw new RuntimeException("Image upload failed", e);
        }
    }
}
