package com.ecom.salezone.services;

import com.ecom.salezone.dtos.PageableResponse;
import com.ecom.salezone.dtos.ProductDto;

import java.util.List;

/**
 * ProductService defines business operations related to
 * product management in the SaleZone E-commerce system.
 *
 * Responsibilities:
 * - Creating products
 * - Bulk product creation
 * - Updating product information
 * - Deleting products
 * - Fetching product details
 * - Searching products with filters
 * - Managing product categories
 * - Fetching live products available for purchase
 *
 * Product Management Features:
 * - Pagination and sorting support
 * - Category based filtering
 * - Price range filtering
 * - Keyword based search
 *
 * @author : Sandeep Kumar Swain
 * @version : 1.0
 * @since : 15-03-2026
 */
public interface ProductService {

    /**
     * Creates a new product.
     *
     * @param productDto product details
     * @param logkey unique request identifier used for tracing logs
     * @return created product details
     */
    ProductDto create(ProductDto productDto, String logkey);

    /**
     * Creates multiple products in bulk.
     *
     * @param productDtos list of products to create
     * @param logkey unique request identifier used for tracing logs
     * @return list of created products
     */
    List<ProductDto> createBulk(List<ProductDto> productDtos, String logkey);

    /**
     * Updates an existing product.
     *
     * @param productDto updated product data
     * @param productId ID of the product to update
     * @param logkey unique request identifier used for tracing logs
     * @return updated product details
     */
    ProductDto update(ProductDto productDto, String productId, String logkey);

    /**
     * Deletes a product from the system.
     *
     * @param productId ID of the product
     * @param logkey unique request identifier used for tracing logs
     */
    void delete(String productId, String logkey);

    /**
     * Fetches details of a specific product.
     *
     * @param productId ID of the product
     * @param logkey unique request identifier used for tracing logs
     * @return product details
     */
    ProductDto get(String productId, String logkey);

    /**
     * Fetches all products with pagination and sorting.
     *
     * @param pageNumber page index
     * @param pageSize number of records per page
     * @param sortBy field used for sorting
     * @param sortDir sorting direction (asc / desc)
     * @param logkey unique request identifier used for tracing logs
     * @return paginated product list
     */
    PageableResponse<ProductDto> getAll(
            int pageNumber,
            int pageSize,
            String sortBy,
            String sortDir,
            String logkey
    );

    /**
     * Fetches only active/live products.
     *
     * @param pageNumber page index
     * @param pageSize number of records per page
     * @param sortBy sorting field
     * @param sortDir sorting direction
     * @param logkey unique request identifier used for tracing logs
     * @return paginated list of live products
     */
    PageableResponse<ProductDto> getAllLive(
            int pageNumber,
            int pageSize,
            String sortBy,
            String sortDir,
            String logkey
    );

    /**
     * Searches products using keyword, category filter,
     * and price range filters.
     *
     * @param query search keyword
     * @param categoryId optional category filter
     * @param minPrice minimum price filter
     * @param maxPrice maximum price filter
     * @param pageNumber page index
     * @param pageSize number of records per page
     * @param sortBy sorting field
     * @param sortDir sorting direction
     * @param logkey unique request identifier used for tracing logs
     *
     * @return paginated search results
     */
    PageableResponse<ProductDto> searchProducts(
            String query,
            String categoryId,
            Double minPrice,
            Double maxPrice,
            int pageNumber,
            int pageSize,
            String sortBy,
            String sortDir,
            String logkey
    );

    /**
     * Creates a product under a specific category.
     *
     * @param productDto product details
     * @param categoryId category ID
     * @param logkey unique request identifier used for tracing logs
     * @return created product details
     */
    ProductDto createWithCategory(ProductDto productDto, String categoryId, String logkey);

    /**
     * Updates the category of an existing product.
     *
     * @param productId product ID
     * @param categoryId new category ID
     * @param logkey unique request identifier used for tracing logs
     * @return updated product details
     */
    ProductDto updateCategory(String productId, String categoryId, String logkey);

    /**
     * Fetches all products belonging to a specific category.
     *
     * @param categoryId category ID
     * @param pageNumber page index
     * @param pageSize number of records per page
     * @param sortBy sorting field
     * @param sortDir sorting direction
     * @param logkey unique request identifier used for tracing logs
     *
     * @return paginated list of products in the category
     */
    PageableResponse<ProductDto> getAllOfCategory(
            String categoryId,
            int pageNumber,
            int pageSize,
            String sortBy,
            String sortDir,
            String logkey
    );
}