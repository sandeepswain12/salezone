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

@Service
public class FileServiceImpl implements FileService {

    private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    @Override
    public String uploadFile(MultipartFile file, String path, String logkey) throws IOException {

        logger.info("{} Starting file upload process",logkey);

        String originalFilename = file.getOriginalFilename();
        logger.info("{} Original file name: {}",logkey, originalFilename);

        String filename = UUID.randomUUID().toString();
        logger.info("{} Generated random file name: {}",logkey, filename);

        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        logger.info("{} File extension extracted: {}",logkey, extension);

        String fileNameWithExtension = filename + extension;
        logger.info("{} Final file name with extension: {}",logkey, fileNameWithExtension);

        String fullPathWithFileName = path + fileNameWithExtension;
        logger.info("{} Full file path: {}",logkey, fullPathWithFileName);

        if (extension.equalsIgnoreCase(".png")
                || extension.equalsIgnoreCase(".jpg")
                || extension.equalsIgnoreCase(".jpeg")) {

            File folder = new File(path);
            if (!folder.exists()) {
                folder.mkdirs();
                logger.info("{} Upload directory created at path: {}",logkey, path);
            }

            logger.info("{} Uploading file to disk...",logkey);
            Files.copy(file.getInputStream(), Paths.get(fullPathWithFileName));

            logger.info("{} File uploaded successfully: {}",logkey, fileNameWithExtension);
            return fileNameWithExtension;

        } else {
            logger.error("{} File upload failed. Unsupported file extension: {}",logkey, extension);
            throw new RuntimeException("{} File with this " + extension + " not allowed !!");
        }
    }

    @Override
    public InputStream getResource(String path, String name, String logkey) throws FileNotFoundException {

        String fullPath = path + File.separator + name;
        logger.info("{} Fetching file resource from path: {}",logkey, fullPath);

        InputStream inputStream = new FileInputStream(fullPath);

        logger.info("{} File resource loaded successfully: {}",logkey, name);
        return inputStream;
    }
}
