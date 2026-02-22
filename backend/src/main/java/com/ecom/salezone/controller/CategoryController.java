package com.ecom.salezone.controller;

import com.ecom.salezone.dtos.ApiResponseMessage;
import com.ecom.salezone.dtos.CategoryDto;
import com.ecom.salezone.dtos.PageableResponse;
import com.ecom.salezone.dtos.ProductDto;
import com.ecom.salezone.util.LogKeyGenerator;
import com.ecom.salezone.services.CategoryService;
import com.ecom.salezone.services.ProductService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/salezone/ecom/categories")
@CrossOrigin(origins = "http://localhost:5173/")
public class CategoryController {

    private static final Logger log =
            LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    // ================= CREATE CATEGORY =================
    @PostMapping("/create")
    public ResponseEntity<CategoryDto> createCategory(
            @Valid @RequestBody CategoryDto categoryDto) {

        String logkey = LogKeyGenerator.generateLogKey();
        log.info("LogKey: {} - Create category request received | payload={}",
                logkey, categoryDto);

        CategoryDto createdCategory =
                categoryService.create(categoryDto, logkey);

        log.info("LogKey: {} - Category created successfully | payload={}",
                logkey, createdCategory);

        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    // ================= UPDATE CATEGORY =================
    @PutMapping("/update/{categoryId}")
    public ResponseEntity<CategoryDto> updateCategory(
            @PathVariable String categoryId,
            @RequestBody CategoryDto categoryDto) {

        String logkey = LogKeyGenerator.generateLogKey();
        log.info("LogKey: {} - Update category request received | categoryId={} payload={}",
                logkey, categoryId, categoryDto);

        CategoryDto updatedCategory =
                categoryService.update(categoryDto, categoryId, logkey);

        log.info("LogKey: {} - Category updated successfully | categoryId={} payload={}",
                logkey, categoryId, updatedCategory);

        return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
    }

    // ================= DELETE CATEGORY =================
    @DeleteMapping("/delete/{categoryId}")
    public ResponseEntity<ApiResponseMessage> deleteCategory(
            @PathVariable String categoryId) {

        String logkey = LogKeyGenerator.generateLogKey();
        log.warn("LogKey: {} - Delete category request received | categoryId={}",
                logkey, categoryId);

        categoryService.delete(categoryId, logkey);

        ApiResponseMessage responseMessage =
                ApiResponseMessage.builder()
                        .message("Category is deleted successfully !!")
                        .status(HttpStatus.OK)
                        .success(true)
                        .build();

        log.info("LogKey: {} - Category deleted successfully | categoryId={}",
                logkey, categoryId);

        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    // ================= GET ALL CATEGORIES =================
    @GetMapping
    public ResponseEntity<PageableResponse<CategoryDto>> getAll(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) throws InterruptedException {

        String logkey = LogKeyGenerator.generateLogKey();
        log.info("LogKey: {} - Get all categories request received | page={} size={} sortBy={} sortDir={}",
                logkey, pageNumber, pageSize, sortBy, sortDir);

        Thread.sleep(1000); // existing logic unchanged

        PageableResponse<CategoryDto> response =
                categoryService.getAll(pageNumber, pageSize, sortBy, sortDir, logkey);

        log.info("LogKey: {} - Categories fetched successfully | totalElements={}",
                logkey, response.getContent().size());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // ================= GET SINGLE CATEGORY =================
    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> getSingle(
            @PathVariable String categoryId) {

        String logkey = LogKeyGenerator.generateLogKey();
        log.info("LogKey: {} - Get category request received | categoryId={}",
                logkey, categoryId);

        CategoryDto categoryDto =
                categoryService.get(categoryId, logkey);

        log.info("LogKey: {} - Category fetched successfully | categoryId={} payload={}",
                logkey, categoryId, categoryDto);

        return ResponseEntity.ok(categoryDto);
    }

    // ================= CREATE PRODUCT UNDER CATEGORY =================
    @PostMapping("/{categoryId}/products")
    public ResponseEntity<ProductDto> createProductWithCategory(
            @PathVariable String categoryId,
            @RequestBody ProductDto dto) {

        String logkey = LogKeyGenerator.generateLogKey();
        log.info("LogKey: {} - Create product under category request received | categoryId={} payload={}",
                logkey, categoryId, dto);

        ProductDto productWithCategory =
                productService.createWithCategory(dto, categoryId, logkey);

        log.info("LogKey: {} - Product created under category successfully | categoryId={} productId={}",
                logkey, categoryId, productWithCategory.getProductId());

        return new ResponseEntity<>(productWithCategory, HttpStatus.CREATED);
    }

    // ================= UPDATE PRODUCT CATEGORY =================
    @PutMapping("/{categoryId}/products/{productId}")
    public ResponseEntity<ProductDto> updateCategoryOfProduct(
            @PathVariable String categoryId,
            @PathVariable String productId) {

        String logkey = LogKeyGenerator.generateLogKey();
        log.info("LogKey: {} - Update product category request received | productId={} categoryId={}",
                logkey, productId, categoryId);

        ProductDto productDto =
                productService.updateCategory(productId, categoryId, logkey);

        log.info("LogKey: {} - Product category updated successfully | productId={} categoryId={}",
                logkey, productId, categoryId);

        return new ResponseEntity<>(productDto, HttpStatus.OK);
    }

    // ================= GET PRODUCTS OF CATEGORY =================
    @GetMapping("/{categoryId}/products")
    public ResponseEntity<PageableResponse<ProductDto>> getProductsOfCategory(
            @PathVariable String categoryId,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {

        String logkey = LogKeyGenerator.generateLogKey();
        log.info("LogKey: {} - Get products of category request received | categoryId={} page={} size={} sortBy={} sortDir={}",
                logkey, categoryId, pageNumber, pageSize, sortBy, sortDir);

        PageableResponse<ProductDto> response =
                productService.getAllOfCategory(
                        categoryId, pageNumber, pageSize, sortBy, sortDir, logkey
                );

        log.info("LogKey: {} - Products fetched successfully for category | categoryId={} totalElements={}",
                logkey, categoryId, response.getContent().size());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}