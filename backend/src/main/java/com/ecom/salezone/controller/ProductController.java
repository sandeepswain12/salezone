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

    // Logger for controller-level request tracing
    private static final Logger log =
            LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @Autowired
    private FileService fileService;

    @Value("${product.image.path}")
    private String imagePath;

    /**
     * Create new product
     * URL: POST /salezone/ecom/products/create
     */
    @PostMapping("/create")
    public ResponseEntity<ProductDto> createProduct(
            @RequestBody ProductDto productDto) {

        String logkey = LogKeyGenerator.generateLogKey();
        log.info("{} api call : Create product | productRequest = {} ",logkey,
                productDto);

        ProductDto createdProduct =
                productService.create(productDto,logkey);

        log.info("{} Product created successfully | productResponse = {}",logkey,
                createdProduct);

        return new ResponseEntity<>(
                createdProduct,
                HttpStatus.CREATED
        );
    }

    /**
     * Update existing product
     * URL: PUT /salezone/ecom/products/update/{productId}
     */
    @PutMapping("/update/{productId}")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable String productId,
            @RequestBody ProductDto productDto) {

        String logkey = LogKeyGenerator.generateLogKey();
        log.info("{} API CALL: Update product | productId={} productRequest = {}",logkey, productId,productDto);

        ProductDto updatedProduct =
                productService.update(productDto, productId,logkey);

        log.info("{} Product updated successfully | productId={} productRespone = {}",logkey, productId,updatedProduct);

        return new ResponseEntity<>(
                updatedProduct,
                HttpStatus.OK
        );
    }

    /**
     * Delete product
     * URL: DELETE /salezone/ecom/products/delete/{productId}
     */
    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<ApiResponseMessage> delete(
            @PathVariable String productId) {

        String logkey = LogKeyGenerator.generateLogKey();
        log.info("{} API CALL: Delete product | productId={}",logkey, productId);

        productService.delete(productId,logkey);

        ApiResponseMessage responseMessage =
                ApiResponseMessage.builder()
                        .message("Product is deleted successfully !!")
                        .status(HttpStatus.OK)
                        .success(true)
                        .build();

        log.info("{} Product deleted successfully | productId={}",logkey, productId);

        return new ResponseEntity<>(
                responseMessage,
                HttpStatus.OK
        );
    }

    /**
     * Get product by id
     * URL: GET /salezone/ecom/products/{productId}
     */
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> getProduct(
            @PathVariable String productId) {

        String logkey = LogKeyGenerator.generateLogKey();
        log.info("API CALL: Get product | productId={}", productId);

        ProductDto productDto =
                productService.get(productId,logkey);

        log.info("Product fetched successfully | productId={}", productId);

        return new ResponseEntity<>(
                productDto,
                HttpStatus.OK
        );
    }

    /**
     * Get all products
     * URL: GET /salezone/ecom/products
     */
    @GetMapping
    public ResponseEntity<PageableResponse<ProductDto>> getAll(
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "title") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {

        String logkey = LogKeyGenerator.generateLogKey();
        log.info("{} API CALL: Get all products | page={} size={}",logkey,
                pageNumber, pageSize);

        PageableResponse<ProductDto> response =
                productService.getAll(pageNumber, pageSize, sortBy, sortDir,logkey);

        log.info("{} Products fetched successfully | count={}",logkey,
                response.getContent().size());

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    /**
     * Get all live products
     * URL: GET /salezone/ecom/products/live
     */
    @GetMapping("/live")
    public ResponseEntity<PageableResponse<ProductDto>> getAllLive(
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "title") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {

        String logkey = LogKeyGenerator.generateLogKey();
        log.info("{} API CALL: Get all live products | page={} size={}",
                logkey, pageNumber, pageSize);

        PageableResponse<ProductDto> response =
                productService.getAllLive(pageNumber, pageSize, sortBy, sortDir,logkey);

        log.info("{} Live products fetched successfully | count={}",
                logkey, response.getContent().size());

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    /**
     * Search product by title
     * URL: GET /salezone/ecom/products/search/{query}
     */
    @GetMapping("/search/{query}")
    public ResponseEntity<PageableResponse<ProductDto>> searchProduct(
            @PathVariable String query,
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "title") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {

        String logkey = LogKeyGenerator.generateLogKey();
        log.info("{} API CALL: Search product | query={} page={} size={}",
                logkey, query, pageNumber, pageSize);

        PageableResponse<ProductDto> response =
                productService.searchByTitle(
                        query, pageNumber, pageSize, sortBy, sortDir,logkey
                );

        log.info("{} Product search completed | resultCount={}",
                logkey, response.getContent().size());

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    /**
     * Upload product image
     * URL: POST /salezone/ecom/products/image/{productId}
     */
    @PostMapping("/image/{productId}")
    public ResponseEntity<ImageResponse> uploadProductImage(
            @PathVariable String productId,
            @RequestParam("productImage") MultipartFile image)
            throws IOException {

        String logkey = LogKeyGenerator.generateLogKey();
        log.info("{} API CALL: Upload product image | productId={} fileName={}",
                logkey, productId, image.getOriginalFilename());

        String fileName =
                fileService.uploadFile(image, imagePath, logkey);

        ProductDto productDto =
                productService.get(productId,logkey);
        productDto.setProductImageName(fileName);

        productService.update(productDto, productId,logkey);

        ImageResponse response =
                ImageResponse.builder()
                        .imageName(fileName)
                        .message("Product image is successfully uploaded !!")
                        .status(HttpStatus.CREATED)
                        .success(true)
                        .build();

        log.info("{} Product image uploaded successfully | productId={}",logkey,
                productId);

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    /**
     * Serve product image
     * URL: GET /salezone/ecom/products/image/{productId}
     */
    @GetMapping("/image/{productId}")
    public void serveUserImage(
            @PathVariable String productId,
            HttpServletResponse response)
            throws IOException {

        String logkey = LogKeyGenerator.generateLogKey();
        log.info("{} API CALL: Serve product image | productId={}",logkey, productId);

        ProductDto productDto =
                productService.get(productId,logkey);

        InputStream resource =
                fileService.getResource(
                        imagePath,
                        productDto.getProductImageName(),
                        logkey
                );

        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        StreamUtils.copy(resource, response.getOutputStream());

        log.info("{} Product image served successfully | productId={}",logkey, productId);
    }
}
