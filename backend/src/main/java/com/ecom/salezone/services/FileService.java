package com.ecom.salezone.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public interface FileService {

    String uploadFile(MultipartFile file, String path, String logkey) throws IOException;

    InputStream getResource(String path, String name, String logkey) throws FileNotFoundException;

}
