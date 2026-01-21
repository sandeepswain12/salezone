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

    Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    @Override
    public String uploadFile(MultipartFile file, String path) throws IOException {

        //abc.png
        String originalFilename = file.getOriginalFilename();
        logger.info("ORIGINAL FILE NAME : {}", originalFilename);
        String filename = UUID.randomUUID().toString();
        logger.info("NEW FILE NAME GENERATED : {}", filename);
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        logger.info("FILE EXTENSION : {}", extension);
        String fileNameWithExtension = filename + extension;
        logger.info("FILE NAME WITH EXTENSION : {}", fileNameWithExtension);
        String fullPathWithFileName = path  + fileNameWithExtension;
        logger.info("FULL PATH WITH FILE NAME : {}", fileNameWithExtension);

        if (extension.equalsIgnoreCase(".png") || extension.equalsIgnoreCase(".jpg") || extension.equalsIgnoreCase(".jpeg")) {

            //file save
            logger.info("FILE EXTENSION : {} ", extension);
            File folder = new File(path);
            if (!folder.exists()) {
                //create the folder
                folder.mkdirs();
                logger.info("FOLDER CREATED ON PATH : {}", path);
            }

            //upload
            Files.copy(file.getInputStream(), Paths.get(fullPathWithFileName));
            return fileNameWithExtension;

        } else {
            logger.info("FILE WITH EXTENSION NOT ALLOWED , EXTENSION: {} ", extension);
            throw new RuntimeException("File with this " + extension + " not allowed !!");
        }


    }

    @Override
    public InputStream getResource(String path, String name) throws FileNotFoundException {
        String fullPath = path + File.separator + name;
        logger.info("FULL PATH WITH FILE NAME : {}", fullPath);
        InputStream inputStream = new FileInputStream(fullPath);
        return inputStream;
    }
}
