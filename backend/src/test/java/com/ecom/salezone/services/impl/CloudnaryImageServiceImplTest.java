package com.ecom.salezone.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CloudnaryImageServiceImplTest {

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private Uploader uploader;

    @InjectMocks
    private CloudnaryImageServiceImpl cloudnaryImageService;

    @Test
    void uploadImage_shouldReturnSecureUrl() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "img".getBytes());

        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(byte[].class), any(Map.class)))
                .thenReturn(Map.of("secure_url", "https://cdn.example.com/test.png"));

        String result = cloudnaryImageService.uploadImage(file, "log-1");

        assertEquals("https://cdn.example.com/test.png", result);
    }

    @Test
    void uploadImage_shouldWrapUploadFailure() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "img".getBytes());

        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(byte[].class), any(Map.class)))
                .thenThrow(new RuntimeException("cloudinary error"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> cloudnaryImageService.uploadImage(file, "log-1")
        );

        assertEquals("Image upload failed", exception.getMessage());
    }
}
