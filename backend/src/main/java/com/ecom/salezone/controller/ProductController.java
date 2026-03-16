package com.ecom.salezone.controller;

import com.ecom.salezone.dtos.ApiResponseMessage;
import com.ecom.salezone.dtos.ImageResponse;
import com.ecom.salezone.dtos.PageableResponse;
import com.ecom.salezone.dtos.ProductDto;
import com.ecom.salezone.services.CloudnaryImageService;
import com.ecom.salezone.util.LogKeyGenerator;
import com.ecom.salezone.services.FileService;
import com.ecom.salezone.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * ProductController handles product management operations
 * in the SaleZone E-commerce system.
 *
 * This controller provides APIs for:
 * - Creating products
 * - Bulk product creation
 * - Updating products
 * - Deleting products
 * - Fetching products
 * - Searching products
 * - Uploading product images
 * - Serving product images
 *
 * Features:
 * - Pagination support
 * - Product search with filters
 * - Category based filtering
 * - Price range filtering
 * - Image upload via Cloudinary
 *
 * Security:
 * - Product creation/update/delete usually require admin privileges.
 * - Product browsing APIs are public.
 *
 * Image Handling:
 * - Images are uploaded to Cloudinary.
 * - Image URLs are stored in the database.
 *
 * @author : Sandeep Kumar Swain
 * @version : 1.0
 * @since : 15-03-2026
 */

@Tag(
        name = "Product APIs",
        description = "APIs for managing products in the SaleZone e-commerce system"
)
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

    @Autowired
    private CloudnaryImageService cloudnaryImageService;

    @Value("${product.image.path}")
    private String imagePath;

    @Operation(
            summary = "Create product",
            description = "Creates a new product in the system."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid product data")
    })
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


    @Operation(
            summary = "Bulk create products",
            description = "Creates multiple products in a single request."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Products created successfully")
    })
    @PostMapping("/create/bulk")
    public ResponseEntity<List<ProductDto>> createProducts(
            @RequestBody List<ProductDto> productDtos) {

        String logkey = LogKeyGenerator.generateLogKey();

        log.info("LogKey: {} - Bulk create product request received | count={}",
                logkey, productDtos.size());

        List<ProductDto> createdProducts =
                productService.createBulk(productDtos, logkey);

        log.info("LogKey: {} - Bulk product creation completed | totalSaved={}",
                logkey, createdProducts.size());

        return new ResponseEntity<>(createdProducts, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Update product",
            description = "Updates an existing product using the product ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
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

    @Operation(
            summary = "Delete product",
            description = "Deletes a product from the system."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
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

    @Operation(
            summary = "Get product by ID",
            description = "Fetches product details using product ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
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

    @Operation(
            summary = "Get all products",
            description = "Fetches all products with pagination and sorting support."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products fetched successfully")
    })
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

    @Operation(
            summary = "Get live products",
            description = "Fetches only active/live products available for purchase."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Live products fetched successfully")
    })
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

    @Operation(
            summary = "Search products",
            description = "Search products using keyword, category filter and price range."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search results fetched successfully")
    })
    @GetMapping("/search/{query}")
    public ResponseEntity<PageableResponse<ProductDto>> searchProduct(
            @PathVariable String query,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        String logkey = LogKeyGenerator.generateLogKey();

        log.info("LogKey: {} - Search product request | query={} category={} minPrice={} maxPrice={}",
                logkey, query, categoryId, minPrice, maxPrice);

        PageableResponse<ProductDto> response =
                productService.searchProducts(query, categoryId, minPrice, maxPrice,
                        pageNumber, pageSize, sortBy, sortDir, logkey);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Upload product image",
            description = "Uploads a product image to Cloudinary and associates it with the product."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Image uploaded successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PostMapping("/image/{productId}")
    public ResponseEntity<ImageResponse> uploadProductImage(
            @PathVariable String productId,
            @RequestParam("productImage") MultipartFile image)
            throws IOException {

        String logkey = LogKeyGenerator.generateLogKey();
        log.info("LogKey: {} - Upload product image request received | productId={} fileName={}",
                logkey, productId, image.getOriginalFilename());

        /*Before we store images in our application

        String fileName =
                fileService.uploadFile(image, imagePath, logkey);


        ProductDto productDto =
                productService.get(productId, logkey);
        productDto.setProductImageName(fileName);

        productService.update(productDto, productId, logkey);
        */

//        Now we are storing images in cloudnary
        String productImageUrl = cloudnaryImageService.uploadImage(image,logkey);

        ProductDto productDto = productService.get(productId,logkey);
        productDto.setProductImageUrl(productImageUrl);

        productService.update(productDto, productId, logkey);

        ImageResponse response =
                ImageResponse.builder()
                        .imageName(productImageUrl)
                        .message("Product image is successfully uploaded !!")
                        .status(HttpStatus.CREATED)
                        .success(true)
                        .build();

        log.info("LogKey: {} - Product image uploaded successfully | productId={} imageName={}",
                logkey, productId, productImageUrl);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Serve product image",
            description = "Streams the product image from server storage."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image served successfully"),
            @ApiResponse(responseCode = "404", description = "Image not found")
    })
    @GetMapping("/image/{productId}")
    public void serveUserImage(
            @PathVariable String productId,
            HttpServletResponse response) throws IOException {

        String logkey = LogKeyGenerator.generateLogKey();

        log.info("LogKey: {} - Serve product image request received | productId={}",
                logkey, productId);

        try {

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

        } catch (FileNotFoundException e) {

            log.error("LogKey: {} - Product image not found | productId={}", logkey, productId);

            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}