package com.ecom.salezone.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cloudinary configuration for the SaleZone application.
 *
 * This configuration creates a Cloudinary bean used for
 * uploading and managing images in the cloud.
 *
 * Images stored in Cloudinary are used for:
 * - Product images
 * - User profile images
 * - Other media assets
 *
 * Credentials are loaded from application properties.
 *
 * Example properties:
 * cloudinary.cloud-name=your_cloud_name
 * cloudinary.api-key=your_api_key
 * cloudinary.api-secret=your_api_secret
 *
 * @author : Sandeep Kumar Swain
 * @version : 1.0
 * @since : 2026
 */
@Configuration
public class CloudinaryConfig {

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    /**
     * Creates a Cloudinary client bean for interacting
     * with the Cloudinary API.
     *
     * @return configured Cloudinary instance
     */
    @Bean
    public Cloudinary cloudinary() {

        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }
}