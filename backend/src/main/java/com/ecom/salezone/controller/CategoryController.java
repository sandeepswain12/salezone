package com.ecom.salezone.controller;

import com.ecom.salezone.dtos.ApiResponseMessage;
import com.ecom.salezone.dtos.CategoryDto;
import com.ecom.salezone.dtos.PageableResponse;
import com.ecom.salezone.dtos.ProductDto;
import com.ecom.salezone.helper.LogKeyGenerator;
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

        String logkey = LogKeyGenerator.generateLogKey();
        log.info("{} API CALL: Create category | categoryRequest={} ",
                logkey, categoryDto);

        CategoryDto createdCategory =
                categoryService.create(categoryDto,logkey);

        log.info("{} Category created successfully | categoryResponse={}",
                logkey, createdCategory);

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

        String logkey = LogKeyGenerator.generateLogKey();
        log.info("{} API CALL: Update category | categoryId={} categoryRequest = {}",logkey, categoryId , categoryDto);

        CategoryDto updatedCategory =
                categoryService.update(categoryDto, categoryId, logkey);

        log.info("{} Category updated successfully | categoryId={} categoryResponse = {}",logkey, categoryId, updatedCategory);

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

        String logkey = LogKeyGenerator.generateLogKey();
        log.info("{} API CALL: Delete category | categoryId={}",logkey, categoryId);

        categoryService.delete(categoryId,logkey);

        ApiResponseMessage responseMessage =
                ApiResponseMessage.builder()
                        .message("Category is deleted successfully !!")
                        .status(HttpStatus.OK)
                        .success(true)
                        .build();

        log.info("{} Category deleted successfully | categoryId={}",logkey, categoryId);

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

        String logkey = LogKeyGenerator.generateLogKey();
        log.info("{} API CALL: Get all categories | page={} size={}",
                logkey, pageNumber, pageSize);

        Thread.sleep(1000); // existing logic (unchanged)

        PageableResponse<CategoryDto> response =
                categoryService.getAll(pageNumber, pageSize, sortBy, sortDir, logkey);

        log.info("{} Categories fetched successfully | count={}",
                logkey, response.getContent().size());

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

        String logkey = LogKeyGenerator.generateLogKey();
        log.info("{} API CALL: Get category | categoryId={}",logkey, categoryId);

        CategoryDto categoryDto =
                categoryService.get(categoryId,logkey);

        log.info("{} Category fetched successfully | categoryId={}",logkey, categoryId);

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

        String logkey = LogKeyGenerator.generateLogKey();
        log.info("{} API CALL: Create product with category | categoryId={}",
                logkey, categoryId);

        ProductDto productWithCategory =
                productService.createWithCategory(dto, categoryId, logkey);

        log.info("{} Product created under category | categoryId={}",
                logkey, categoryId);

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

        String logkey = LogKeyGenerator.generateLogKey();
        log.info("{} API CALL: Update product category | productId={} categoryId={}",
                logkey, productId, categoryId);

        ProductDto productDto =
                productService.updateCategory(productId, categoryId,logkey);

        log.info("{} Product category updated successfully | productId={}",
                logkey, productId);

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

        String logkey = LogKeyGenerator.generateLogKey();
        log.info("{} API CALL: Get products of category | categoryId={} page={} size={}",
                logkey, categoryId, pageNumber, pageSize);

        PageableResponse<ProductDto> response =
                productService.getAllOfCategory(
                        categoryId, pageNumber, pageSize, sortBy, sortDir,logkey
                );

        log.info("{} Products fetched successfully | categoryId={} count={}",
                logkey, categoryId, response.getContent().size());

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }
}

