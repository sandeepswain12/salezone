package com.ecom.salezone.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * FileService defines operations related to file handling
 * within the SaleZone E-commerce system.
 *
 * Responsibilities:
 * - Upload files to local server storage
 * - Retrieve stored files as input streams
 *
 * This service is mainly used for handling image uploads
 * and serving stored files such as product or user images.
 *
 * Note:
 * In production environments, cloud storage solutions
 * like Cloudinary, AWS S3, or Google Cloud Storage are
 * typically preferred over local file storage.
 *
 * @author : Sandeep Kumar Swain
 * @version : 1.0
 * @since : 15-03-2026
 */
public interface FileService {

    /**
     * Uploads a file to the specified server path.
     *
     * @param file    file to be uploaded
     * @param path    destination path where the file will be stored
     * @param logkey  unique request identifier used for tracing logs
     *
     * @return name of the uploaded file
     *
     * @throws IOException if an error occurs during file upload
     */
    String uploadFile(MultipartFile file, String path, String logkey) throws IOException;

    /**
     * Retrieves a file as an InputStream from the specified path.
     *
     * @param path   location of the file storage
     * @param name   name of the file to retrieve
     * @param logkey unique request identifier used for tracing logs
     *
     * @return InputStream of the requested file
     *
     * @throws FileNotFoundException if the requested file does not exist
     */
    InputStream getResource(String path, String name, String logkey) throws FileNotFoundException;
}