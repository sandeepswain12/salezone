package com.ecom.salezone.services;

import org.springframework.web.multipart.MultipartFile;

public interface CloudnaryImageService {
    String uploadImage(MultipartFile file, String logKey);
}
