package com.ecom.salezone.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileServiceImplTest {

    private final FileServiceImpl fileService = new FileServiceImpl();

    @TempDir
    Path tempDir;

    @Test
    void uploadFile_shouldStoreImageAndReturnGeneratedName() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar.jpg", "image/jpeg", "image-data".getBytes(StandardCharsets.UTF_8)
        );

        String generatedName = fileService.uploadFile(file, tempDir.toString() + "\\", "log-1");

        assertTrue(generatedName.endsWith(".jpg"));
        assertTrue(Files.exists(tempDir.resolve(generatedName)));
    }

    @Test
    void getResource_shouldThrowWhenFileDoesNotExist() {
        FileNotFoundException exception = assertThrows(
                FileNotFoundException.class,
                () -> fileService.getResource(tempDir.toString(), "missing.jpg", "log-1")
        );

        assertEquals("Image not found: missing.jpg", exception.getMessage());
    }
}
