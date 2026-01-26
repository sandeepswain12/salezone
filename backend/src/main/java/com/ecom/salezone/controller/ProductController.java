package com.ecom.salezone.controller;

import com.ecom.salezone.dtos.ApiResponseMessage;
import com.ecom.salezone.dtos.ImageResponse;
import com.ecom.salezone.dtos.PageableResponse;
import com.ecom.salezone.dtos.ProductDto;
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

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @Autowired
    private FileService fileService;

    @Value("${product.image.path}")
    private String imagePath;

    // ================= CREATE PRODUCT =================
    @PostMapping("/create")
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productDto) {

        log.info("Creating product with title: {}", productDto.getTitle());

        ProductDto createdProduct = productService.create(productDto);

        log.info("Product created successfully with ID: {}", createdProduct.getProductId());

        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    // ================= UPDATE PRODUCT =================
    @PutMapping("update/{productId}")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable String productId,
            @RequestBody ProductDto productDto) {

        log.info("Updating product with ID: {}", productId);

        ProductDto updatedProduct = productService.update(productDto, productId);

        log.info("Product updated successfully with ID: {}", productId);

        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    // ================= DELETE PRODUCT =================
    @DeleteMapping("delete/{productId}")
    public ResponseEntity<ApiResponseMessage> delete(@PathVariable String productId) {

        log.warn("Deleting product with ID: {}", productId);

        productService.delete(productId);

        log.info("Product deleted successfully with ID: {}", productId);

        ApiResponseMessage responseMessage = ApiResponseMessage.builder()
                .message("Product is deleted successfully !!")
                .status(HttpStatus.OK)
                .success(true)
                .build();

        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    // ================= GET SINGLE PRODUCT =================
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable String productId) {

        log.debug("Fetching product with ID: {}", productId);

        ProductDto productDto = productService.get(productId);

        log.info("Product fetched successfully with ID: {}", productId);

        return new ResponseEntity<>(productDto, HttpStatus.OK);
    }

    // ================= GET ALL PRODUCTS =================
    @GetMapping
    public ResponseEntity<PageableResponse<ProductDto>> getAll(
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "title") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {

        log.debug("Fetching all products | page={} size={} sortBy={} sortDir={}",
                pageNumber, pageSize, sortBy, sortDir);

        PageableResponse<ProductDto> pageableResponse =
                productService.getAll(pageNumber, pageSize, sortBy, sortDir);

        log.info("Fetched {} products", pageableResponse.getContent().size());

        return new ResponseEntity<>(pageableResponse, HttpStatus.OK);
    }

    // ================= GET ALL LIVE PRODUCTS =================
    @GetMapping("/live")
    public ResponseEntity<PageableResponse<ProductDto>> getAllLive(
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "title") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {

        log.debug("Fetching live products | page={} size={} sortBy={} sortDir={}",
                pageNumber, pageSize, sortBy, sortDir);

        PageableResponse<ProductDto> pageableResponse =
                productService.getAllLive(pageNumber, pageSize, sortBy, sortDir);

        log.info("Fetched {} live products", pageableResponse.getContent().size());

        return new ResponseEntity<>(pageableResponse, HttpStatus.OK);
    }

    // ================= SEARCH PRODUCT =================
    @GetMapping("/search/{query}")
    public ResponseEntity<PageableResponse<ProductDto>> searchProduct(
            @PathVariable String query,
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "title") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {

        log.debug("Searching products with query='{}' | page={} size={}",
                query, pageNumber, pageSize);

        PageableResponse<ProductDto> pageableResponse =
                productService.searchByTitle(query, pageNumber, pageSize, sortBy, sortDir);

        log.info("Search completed. Found {} products", pageableResponse.getContent().size());

        return new ResponseEntity<>(pageableResponse, HttpStatus.OK);
    }

    // ================= UPLOAD PRODUCT IMAGE =================
    @PostMapping("/image/{productId}")
    public ResponseEntity<ImageResponse> uploadProductImage(
            @PathVariable String productId,
            @RequestParam("productImage") MultipartFile image) throws IOException {

        log.info("Uploading image for product ID: {}", productId);
        log.debug("Image name: {}, size: {} bytes",
                image.getOriginalFilename(), image.getSize());

        String fileName = fileService.uploadFile(image, imagePath);

        ProductDto productDto = productService.get(productId);
        productDto.setProductImageName(fileName);

        ProductDto updatedProduct = productService.update(productDto, productId);

        log.info("Image uploaded successfully for product ID: {}", productId);

        ImageResponse response = ImageResponse.builder()
                .imageName(updatedProduct.getProductImageName())
                .message("Product image is successfully uploaded !!")
                .status(HttpStatus.CREATED)
                .success(true)
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // ================= SERVE PRODUCT IMAGE =================
    @GetMapping(value = "/image/{productId}")
    public void serveUserImage(@PathVariable String productId,
                               HttpServletResponse response) throws IOException {

        log.debug("Serving image for product ID: {}", productId);

        ProductDto productDto = productService.get(productId);
        InputStream resource =
                fileService.getResource(imagePath, productDto.getProductImageName());

        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        StreamUtils.copy(resource, response.getOutputStream());

        log.info("Image served successfully for product ID: {}", productId);
    }
}
