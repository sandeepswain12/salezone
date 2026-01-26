package com.ecom.salezone.controller;

import com.ecom.salezone.dtos.ApiResponseMessage;
import com.ecom.salezone.dtos.CategoryDto;
import com.ecom.salezone.dtos.PageableResponse;
import com.ecom.salezone.dtos.ProductDto;
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

    private static final Logger log = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    // ================= CREATE CATEGORY =================
    @PostMapping("/create")
    public ResponseEntity<CategoryDto> createCategory(
            @Valid @RequestBody CategoryDto categoryDto) {

        log.info("Creating category with title: {}", categoryDto.getTitle());

        CategoryDto categoryDto1 = categoryService.create(categoryDto);

        log.info("Category created successfully with ID: {}", categoryDto1.getCategoryId());

        return new ResponseEntity<>(categoryDto1, HttpStatus.CREATED);
    }

    // ================= UPDATE CATEGORY =================
    @PutMapping("/update/{categoryId}")
    public ResponseEntity<CategoryDto> updateCategory(
            @PathVariable String categoryId,
            @RequestBody CategoryDto categoryDto) {

        log.info("Updating category with ID: {}", categoryId);

        CategoryDto updatedCategory = categoryService.update(categoryDto, categoryId);

        log.info("Category updated successfully with ID: {}", categoryId);

        return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
    }

    // ================= DELETE CATEGORY =================
    @DeleteMapping("delete/{categoryId}")
    public ResponseEntity<ApiResponseMessage> deleteCategory(
            @PathVariable String categoryId) {

        log.warn("Deleting category with ID: {}", categoryId);

        categoryService.delete(categoryId);

        log.info("Category deleted successfully with ID: {}", categoryId);

        ApiResponseMessage responseMessage = ApiResponseMessage.builder()
                .message("Category is deleted successfully !!")
                .status(HttpStatus.OK)
                .success(true)
                .build();

        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    // ================= GET ALL CATEGORIES =================
    @GetMapping
    public ResponseEntity<PageableResponse<CategoryDto>> getAll(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "title", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
    ) throws InterruptedException {

        log.debug("Fetching all categories | page={} size={} sortBy={} sortDir={}",
                pageNumber, pageSize, sortBy, sortDir);

        Thread.sleep(1000);

        PageableResponse<CategoryDto> pageableResponse =
                categoryService.getAll(pageNumber, pageSize, sortBy, sortDir);

        log.info("Fetched {} categories", pageableResponse.getContent().size());

        return new ResponseEntity<>(pageableResponse, HttpStatus.OK);
    }

    // ================= GET SINGLE CATEGORY =================
    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> getSingle(@PathVariable String categoryId) {

        log.debug("Fetching category with ID: {}", categoryId);

        CategoryDto categoryDto = categoryService.get(categoryId);

        log.info("Category fetched successfully with ID: {}", categoryId);

        return ResponseEntity.ok(categoryDto);
    }

    // ================= CREATE PRODUCT WITH CATEGORY =================
    @PostMapping("/{categoryId}/products")
    public ResponseEntity<ProductDto> createProductWithCategory(
            @PathVariable("categoryId") String categoryId,
            @RequestBody ProductDto dto) {

        log.info("Creating product under category ID: {}", categoryId);

        ProductDto productWithCategory =
                productService.createWithCategory(dto, categoryId);

        log.info("Product created under category ID: {}", categoryId);

        return new ResponseEntity<>(productWithCategory, HttpStatus.CREATED);
    }

    // ================= UPDATE CATEGORY OF PRODUCT =================
    @PutMapping("/{categoryId}/products/{productId}")
    public ResponseEntity<ProductDto> updateCategoryOfProduct(
            @PathVariable String categoryId,
            @PathVariable String productId) {

        log.info("Updating category of product ID: {} to category ID: {}",
                productId, categoryId);

        ProductDto productDto =
                productService.updateCategory(productId, categoryId);

        log.info("Product category updated successfully | productId={}", productId);

        return new ResponseEntity<>(productDto, HttpStatus.OK);
    }

    // ================= GET PRODUCTS OF CATEGORY =================
    @GetMapping("/{categoryId}/products")
    public ResponseEntity<PageableResponse<ProductDto>> getProductsOfCategory(
            @PathVariable String categoryId,
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "title", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
    ) {

        log.debug("Fetching products for category ID: {} | page={} size={}",
                categoryId, pageNumber, pageSize);

        PageableResponse<ProductDto> response =
                productService.getAllOfCategory(categoryId, pageNumber, pageSize, sortBy, sortDir);

        log.info("Fetched {} products for category ID: {}",
                response.getContent().size(), categoryId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
