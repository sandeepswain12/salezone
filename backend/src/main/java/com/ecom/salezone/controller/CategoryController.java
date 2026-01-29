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

    // Logger for controller-level request tracing
    private static final Logger log =
            LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    /**
     * Create new category
     * URL: POST /salezone/ecom/categories/create
     */
    @PostMapping("/create")
    public ResponseEntity<CategoryDto> createCategory(
            @Valid @RequestBody CategoryDto categoryDto) {

        log.info("API CALL: Create category | title={}",
                categoryDto.getTitle());

        CategoryDto createdCategory =
                categoryService.create(categoryDto);

        log.info("Category created successfully | categoryId={}",
                createdCategory.getCategoryId());

        return new ResponseEntity<>(
                createdCategory,
                HttpStatus.CREATED
        );
    }

    /**
     * Update category
     * URL: PUT /salezone/ecom/categories/update/{categoryId}
     */
    @PutMapping("/update/{categoryId}")
    public ResponseEntity<CategoryDto> updateCategory(
            @PathVariable String categoryId,
            @RequestBody CategoryDto categoryDto) {

        log.info("API CALL: Update category | categoryId={}", categoryId);

        CategoryDto updatedCategory =
                categoryService.update(categoryDto, categoryId);

        log.info("Category updated successfully | categoryId={}", categoryId);

        return new ResponseEntity<>(
                updatedCategory,
                HttpStatus.OK
        );
    }

    /**
     * Delete category
     * URL: DELETE /salezone/ecom/categories/delete/{categoryId}
     */
    @DeleteMapping("/delete/{categoryId}")
    public ResponseEntity<ApiResponseMessage> deleteCategory(
            @PathVariable String categoryId) {

        log.info("API CALL: Delete category | categoryId={}", categoryId);

        categoryService.delete(categoryId);

        ApiResponseMessage responseMessage =
                ApiResponseMessage.builder()
                        .message("Category is deleted successfully !!")
                        .status(HttpStatus.OK)
                        .success(true)
                        .build();

        log.info("Category deleted successfully | categoryId={}", categoryId);

        return new ResponseEntity<>(
                responseMessage,
                HttpStatus.OK
        );
    }

    /**
     * Get all categories
     * URL: GET /salezone/ecom/categories
     */
    @GetMapping
    public ResponseEntity<PageableResponse<CategoryDto>> getAll(
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "title") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir
    ) throws InterruptedException {

        log.info("API CALL: Get all categories | page={} size={}",
                pageNumber, pageSize);

        Thread.sleep(1000); // existing logic (unchanged)

        PageableResponse<CategoryDto> response =
                categoryService.getAll(pageNumber, pageSize, sortBy, sortDir);

        log.info("Categories fetched successfully | count={}",
                response.getContent().size());

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    /**
     * Get single category
     * URL: GET /salezone/ecom/categories/{categoryId}
     */
    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> getSingle(
            @PathVariable String categoryId) {

        log.info("API CALL: Get category | categoryId={}", categoryId);

        CategoryDto categoryDto =
                categoryService.get(categoryId);

        log.info("Category fetched successfully | categoryId={}", categoryId);

        return ResponseEntity.ok(categoryDto);
    }

    /**
     * Create product under a category
     * URL: POST /salezone/ecom/categories/{categoryId}/products
     */
    @PostMapping("/{categoryId}/products")
    public ResponseEntity<ProductDto> createProductWithCategory(
            @PathVariable String categoryId,
            @RequestBody ProductDto dto) {

        log.info("API CALL: Create product with category | categoryId={}",
                categoryId);

        ProductDto productWithCategory =
                productService.createWithCategory(dto, categoryId);

        log.info("Product created under category | categoryId={}",
                categoryId);

        return new ResponseEntity<>(
                productWithCategory,
                HttpStatus.CREATED
        );
    }

    /**
     * Update category of a product
     * URL: PUT /salezone/ecom/categories/{categoryId}/products/{productId}
     */
    @PutMapping("/{categoryId}/products/{productId}")
    public ResponseEntity<ProductDto> updateCategoryOfProduct(
            @PathVariable String categoryId,
            @PathVariable String productId) {

        log.info("API CALL: Update product category | productId={} categoryId={}",
                productId, categoryId);

        ProductDto productDto =
                productService.updateCategory(productId, categoryId);

        log.info("Product category updated successfully | productId={}",
                productId);

        return new ResponseEntity<>(
                productDto,
                HttpStatus.OK
        );
    }

    /**
     * Get all products of a category
     * URL: GET /salezone/ecom/categories/{categoryId}/products
     */
    @GetMapping("/{categoryId}/products")
    public ResponseEntity<PageableResponse<ProductDto>> getProductsOfCategory(
            @PathVariable String categoryId,
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "title") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir
    ) {

        log.info("API CALL: Get products of category | categoryId={} page={} size={}",
                categoryId, pageNumber, pageSize);

        PageableResponse<ProductDto> response =
                productService.getAllOfCategory(
                        categoryId, pageNumber, pageSize, sortBy, sortDir
                );

        log.info("Products fetched successfully | categoryId={} count={}",
                categoryId, response.getContent().size());

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }
}

