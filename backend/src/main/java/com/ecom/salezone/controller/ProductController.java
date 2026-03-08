package com.ecom.salezone.controller;

import com.ecom.salezone.dtos.ApiResponseMessage;
import com.ecom.salezone.dtos.ImageResponse;
import com.ecom.salezone.dtos.PageableResponse;
import com.ecom.salezone.dtos.ProductDto;
import com.ecom.salezone.util.LogKeyGenerator;
import com.ecom.salezone.services.FileService;
import com.ecom.salezone.services.ProductService;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/salezone/ecom/products")
@CrossOrigin(origins = "http://localhost:5173/")
public class ProductController {

    private static final Logger log =
            LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @Autowired
    private FileService fileService;

    @Value("${product.image.path}")
    private String imagePath;

    // ================= CREATE PRODUCT =================
    @PostMapping("/create")
    public ResponseEntity<ProductDto> createProduct(
            @RequestBody ProductDto productDto) {

        String logkey = LogKeyGenerator.generateLogKey();
        log.info("LogKey: {} - Create product request received | payload={}",
                logkey, productDto);

        ProductDto createdProduct =
                productService.create(productDto, logkey);

        log.info("LogKey: {} - Product created successfully | payload={}",
                logkey, createdProduct);

        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    // ================= UPDATE PRODUCT =================
    @PutMapping("/update/{productId}")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable String productId,
            @RequestBody ProductDto productDto) {

        String logkey = LogKeyGenerator.generateLogKey();
        log.info("LogKey: {} - Update product request received | productId={} payload={}",
                logkey, productId, productDto);

        ProductDto updatedProduct =
                productService.update(productDto, productId, logkey);

        log.info("LogKey: {} - Product updated successfully | productId={} payload={}",
                logkey, productId, updatedProduct);

        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    // ================= DELETE PRODUCT =================
    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<ApiResponseMessage> delete(
            @PathVariable String productId) {

        String logkey = LogKeyGenerator.generateLogKey();
        log.info("LogKey: {} - Delete product request received | productId={}",
                logkey, productId);

        productService.delete(productId, logkey);

        ApiResponseMessage responseMessage =
                ApiResponseMessage.builder()
                        .message("Product is deleted successfully !!")
                        .status(HttpStatus.OK)
                        .success(true)
                        .build();

        log.info("LogKey: {} - Product deleted successfully | productId={}",
                logkey, productId);

        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    // ================= GET PRODUCT BY ID =================
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> getProduct(
            @PathVariable String productId) {

        String logkey = LogKeyGenerator.generateLogKey();
        log.info("LogKey: {} - Get product request received | productId={}",
                logkey, productId);

        ProductDto productDto =
                productService.get(productId, logkey);

        log.info("LogKey: {} - Product fetched successfully | productId={} payload={}",
                logkey, productId, productDto);

        return new ResponseEntity<>(productDto, HttpStatus.OK);
    }

    // ================= GET ALL PRODUCTS =================
    @GetMapping
    public ResponseEntity<PageableResponse<ProductDto>> getAll(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        String logkey = LogKeyGenerator.generateLogKey();
        log.info("LogKey: {} - Get all products request received | page={} size={} sortBy={} sortDir={}",
                logkey, pageNumber, pageSize, sortBy, sortDir);

        PageableResponse<ProductDto> response =
                productService.getAll(pageNumber, pageSize, sortBy, sortDir, logkey);

        log.info("LogKey: {} - Products fetched successfully | totalElements={}",
                logkey, response.getContent().size());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // ================= GET ALL LIVE PRODUCTS =================
    @GetMapping("/live")
    public ResponseEntity<PageableResponse<ProductDto>> getAllLive(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        String logkey = LogKeyGenerator.generateLogKey();
        log.info("LogKey: {} - Get all live products request received | page={} size={} sortBy={} sortDir={}",
                logkey, pageNumber, pageSize, sortBy, sortDir);

        PageableResponse<ProductDto> response =
                productService.getAllLive(pageNumber, pageSize, sortBy, sortDir, logkey);

        log.info("LogKey: {} - Live products fetched successfully | totalElements={}",
                logkey, response.getContent().size());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // ================= SEARCH PRODUCT =================
    @GetMapping("/search/{query}")
    public ResponseEntity<PageableResponse<ProductDto>> searchProduct(
            @PathVariable String query,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        String logkey = LogKeyGenerator.generateLogKey();
        log.info("LogKey: {} - Search product request received | query={} page={} size={} sortBy={} sortDir={}",
                logkey, query, pageNumber, pageSize, sortBy, sortDir);

        PageableResponse<ProductDto> response =
                productService.searchByTitle(query, pageNumber, pageSize, sortBy, sortDir, logkey);

        log.info("LogKey: {} - Product search completed successfully | resultCount={}",
                logkey, response.getContent().size());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // ================= UPLOAD PRODUCT IMAGE =================
    @PostMapping("/image/{productId}")
    public ResponseEntity<ImageResponse> uploadProductImage(
            @PathVariable String productId,
            @RequestParam("productImage") MultipartFile image)
            throws IOException {

        String logkey = LogKeyGenerator.generateLogKey();
        log.info("LogKey: {} - Upload product image request received | productId={} fileName={}",
                logkey, productId, image.getOriginalFilename());

        String fileName =
                fileService.uploadFile(image, imagePath, logkey);

        ProductDto productDto =
                productService.get(productId, logkey);
        productDto.setProductImageName(fileName);

        productService.update(productDto, productId, logkey);

        ImageResponse response =
                ImageResponse.builder()
                        .imageName(fileName)
                        .message("Product image is successfully uploaded !!")
                        .status(HttpStatus.CREATED)
                        .success(true)
                        .build();

        log.info("LogKey: {} - Product image uploaded successfully | productId={} imageName={}",
                logkey, productId, fileName);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // ================= SERVE PRODUCT IMAGE =================
    @GetMapping("/image/{productId}")
    public void serveUserImage(
            @PathVariable String productId,
            HttpServletResponse response) throws IOException {

        String logkey = LogKeyGenerator.generateLogKey();

        log.info("LogKey: {} - Serve product image request received | productId={}",
                logkey, productId);

        ProductDto productDto = productService.get(productId, logkey);

        InputStream resource = fileService.getResource(
                imagePath,
                productDto.getProductImageName(),
                logkey
        );

        response.setContentType(MediaType.IMAGE_PNG_VALUE);

        StreamUtils.copy(resource, response.getOutputStream());

        log.info("LogKey: {} - Product image served successfully | productId={} imageName={}",
                logkey, productId, productDto.getProductImageName());
    }
}