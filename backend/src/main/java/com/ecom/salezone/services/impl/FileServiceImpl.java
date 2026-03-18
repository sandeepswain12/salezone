package com.ecom.salezone.services.impl;

import com.ecom.salezone.services.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Implementation of FileService for the SaleZone E-commerce system.
 *
 * Provides functionality for:
 * - Uploading image files to the server
 * - Retrieving stored files as resources
 *
 * Generates unique file names using UUID to avoid collisions
 * and validates allowed image extensions.
 *
 * @author Sandeep Kumar Swain
 * @version 1.0
 * @since 15-03-2026
 */
@Service
public class FileServiceImpl implements FileService {

    private static final Logger logger =
            LoggerFactory.getLogger(FileServiceImpl.class);

    /* Upload File */
    @Override
    public String uploadFile(MultipartFile file, String path, String logkey) throws IOException {

        logger.info("LogKey: {} - Entry into uploadFile method", logkey);

        String originalFilename = file.getOriginalFilename();
        logger.info("LogKey: {} - Original file name | fileName={}", logkey, originalFilename);

        String filename = UUID.randomUUID().toString();
        logger.info("LogKey: {} - Generated random file name | uuid={}", logkey, filename);

        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        logger.info("LogKey: {} - File extension extracted | extension={}", logkey, extension);

        String fileNameWithExtension = filename + extension;
        logger.info("LogKey: {} - Final file name generated | fileName={}", logkey, fileNameWithExtension);

        String fullPathWithFileName = path + fileNameWithExtension;
        logger.info("LogKey: {} - Full file path prepared | path={}", logkey, fullPathWithFileName);

        if (extension.equalsIgnoreCase(".png")
                || extension.equalsIgnoreCase(".jpg")
                || extension.equalsIgnoreCase(".jpeg")) {

            File folder = new File(path);
            if (!folder.exists()) {
                folder.mkdirs();
                logger.info("LogKey: {} - Upload directory created | directory={}", logkey, path);
            }

            logger.info("LogKey: {} - Uploading file to disk...", logkey);
            Files.copy(file.getInputStream(), Paths.get(fullPathWithFileName));

            logger.info("LogKey: {} - File uploaded successfully | fileName={}",
                    logkey, fileNameWithExtension);

            return fileNameWithExtension;

        } else {
            logger.error("LogKey: {} - File upload failed | unsupported extension={}",
                    logkey, extension);
            throw new RuntimeException("{} File with this " + extension + " not allowed !!");
        }
    }

    /* Get Resource */
    @Override
    public InputStream getResource(String path, String name, String logkey)
            throws FileNotFoundException {

        String fullPath = path + File.separator + name;

        logger.info("LogKey: {} - Entry into getResource method | path={}", logkey, fullPath);

        File file = new File(fullPath);

        if (!file.exists()) {

            logger.error("LogKey: {} - Image not found | fileName={}", logkey, name);

            throw new FileNotFoundException("Image not found: " + name);
        }

        logger.info("LogKey: {} - File resource loaded successfully | fileName={}", logkey, name);

        return new FileInputStream(file);
    }
}