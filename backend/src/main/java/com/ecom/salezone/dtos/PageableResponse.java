package com.ecom.salezone.dtos;

import lombok.*;

import java.util.List;

/**
 * Generic response DTO used for paginated API responses.
 * Wraps paginated data along with pagination metadata.
 *
 * @param <T> Type of content returned in the page
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageableResponse<T> {

    /**
     * List of items returned for the current page.
     * Example: List of products, users, orders, etc.
     */
    private List<T> content;

    /**
     * Current page number (1-based index).
     * Example: pageNumber = 1 means first page.
     */
    private int pageNumber;

    /**
     * Number of items per page.
     * Example: pageSize = 10
     */
    private int pageSize;

    /**
     * Total number of elements across all pages.
     */
    private long totalElements;

    /**
     * Total number of pages available.
     */
    private int totalPages;

    /**
     * Indicates whether the current page is the last page.
     * true  → this is the final page
     * false → more pages are available
     */
    private boolean lastPage;

}
