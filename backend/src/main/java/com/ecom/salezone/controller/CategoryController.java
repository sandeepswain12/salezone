package com.ecom.salezone.controller;

import com.ecom.salezone.dtos.ApiResponseMessage;
import com.ecom.salezone.dtos.CategoryDto;
import com.ecom.salezone.dtos.PageableResponse;
import com.ecom.salezone.dtos.ProductDto;
import com.ecom.salezone.util.LogKeyGenerator;
import com.ecom.salezone.services.CategoryService;
import com.ecom.salezone.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * CategoryController handles all category related operations
 * in the SaleZone E-commerce system.
 *
 * This controller provides APIs for:
 * - Creating categories
 * - Updating categories
 * - Deleting categories
 * - Fetching all categories with pagination
 * - Fetching a single category
 * - Creating products under a category
 * - Updating category of a product
 * - Fetching products belonging to a category
 *
 * Features:
 * - Pagination support for category listing
 * - Category based product management
 * - Sorting and filtering capabilities
 *
 * Security:
 * - Some endpoints may require admin privileges
 * - Category management operations are typically restricted
 *
 * @author : Sandeep Kumar Swain
 * @version : 1.0
 * @since : 15-03-2026
 */

@Tag(
        name = "Category APIs",
        description = "APIs for managing product categories in the SaleZone system"
)
@RestController
@RequestMapping("/salezone/ecom/categories")
@CrossOrigin(origins = "http://localhost:5173/")
public class CategoryController {

    private static final Logger log = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Operation(
            summary = "Create category",
            description = "Creates a new product category in the system."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Category created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid category data")
    })
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

    @Operation(
            summary = "Update category",
            description = "Updates an existing category using the category ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category updated successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
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

    @Operation(
            summary = "Delete category",
            description = "Deletes a category from the system."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
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

    @Operation(
            summary = "Get all categories",
            description = "Fetches all categories with pagination and sorting support."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categories fetched successfully")
    })
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

    @Operation(
            summary = "Get category by ID",
            description = "Fetches a single category using the category ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
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

    @Operation(
            summary = "Create product under category",
            description = "Creates a new product under a specific category."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
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

    @Operation(
            summary = "Update product category",
            description = "Updates the category of an existing product."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product category updated successfully"),
            @ApiResponse(responseCode = "404", description = "Product or category not found")
    })
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

    @Operation(
            summary = "Get products of category",
            description = "Fetches all products belonging to a specific category with pagination."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
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