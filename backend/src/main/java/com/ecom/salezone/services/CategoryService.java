package com.ecom.salezone.services;

import com.ecom.salezone.dtos.CategoryDto;
import com.ecom.salezone.dtos.PageableResponse;

/**
 * CategoryService defines business operations related to
 * category management in the SaleZone E-commerce system.
 *
 * Responsibilities:
 * - Creating new product categories
 * - Updating existing categories
 * - Deleting categories
 * - Fetching category details
 * - Fetching all categories with pagination and sorting
 *
 * Categories are used to organize products within the system
 * and help users browse products efficiently.
 *
 * @author : Sandeep Kumar Swain
 * @version : 1.0
 * @since : 15-03-2026
 */
public interface CategoryService {

    /**
     * Creates a new product category.
     *
     * @param categoryDto category details to be created
     * @param logkey unique request identifier used for tracing logs
     *
     * @return CategoryDto containing the created category details
     */
    CategoryDto create(CategoryDto categoryDto, String logkey);

    /**
     * Updates an existing category.
     *
     * @param categoryDto updated category information
     * @param categoryId ID of the category to update
     * @param logkey unique request identifier used for tracing logs
     *
     * @return CategoryDto containing the updated category details
     */
    CategoryDto update(CategoryDto categoryDto, String categoryId, String logkey);

    /**
     * Deletes a category from the system.
     *
     * @param categoryId ID of the category to delete
     * @param logkey unique request identifier used for tracing logs
     */
    void delete(String categoryId, String logkey);

    /**
     * Fetches all categories with pagination and sorting support.
     *
     * @param pageNumber page index (starting from 0)
     * @param pageSize number of records per page
     * @param sortBy field used for sorting
     * @param sortDir sorting direction (asc or desc)
     * @param logkey unique request identifier used for tracing logs
     *
     * @return PageableResponse containing paginated category data
     */
    PageableResponse<CategoryDto> getAll(
            int pageNumber,
            int pageSize,
            String sortBy,
            String sortDir,
            String logkey
    );

    /**
     * Fetches details of a specific category.
     *
     * @param categoryId ID of the category
     * @param logkey unique request identifier used for tracing logs
     *
     * @return CategoryDto containing category details
     */
    CategoryDto get(String categoryId, String logkey);
}