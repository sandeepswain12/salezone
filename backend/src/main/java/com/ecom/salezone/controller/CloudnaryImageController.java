package com.ecom.salezone.controller;

import com.ecom.salezone.services.CloudnaryImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/images")
public class CloudnaryImageController {

    private final CloudnaryImageService cloudinaryService;

    public CloudnaryImageController(CloudnaryImageService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(
            @RequestParam("image") MultipartFile file
    ) {

        String imageUrl = cloudinaryService.uploadImage(file,"dfd");

        return ResponseEntity.ok(imageUrl);
    }
}
