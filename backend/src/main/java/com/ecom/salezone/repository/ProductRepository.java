package com.ecom.salezone.repository;

import com.ecom.salezone.enities.Category;
import com.ecom.salezone.enities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Product entities.
 *
 * Provides database operations for products including:
 * - Searching products by title
 * - Filtering products by category
 * - Filtering products by price range
 * - Fetching only live products
 * - Pagination support
 *
 * Extends JpaRepository which automatically provides
 * standard CRUD database operations.
 *
 * @author : Sandeep Kumar Swain
 * @since : 15-03-2026
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    /**
     * Search products by title containing a keyword.
     *
     * @param subTitle keyword to search in product title
     * @param pageable pagination information
     * @return paginated list of matching products
     */
    Page<Product> findByTitleContaining(String subTitle, Pageable pageable);

    /**
     * Fetch only products that are marked as live.
     *
     * @param pageable pagination information
     * @return paginated list of live products
     */
    Page<Product> findByLiveTrue(Pageable pageable);

    /**
     * Fetch products belonging to a specific category.
     *
     * @param category category entity
     * @param pageable pagination information
     * @return paginated list of products in the category
     */
    Page<Product> findByCategory(Category category, Pageable pageable);

    /**
     * Search products by title, category and price range.
     *
     * @param title product title keyword
     * @param categoryId category ID
     * @param minPrice minimum price
     * @param maxPrice maximum price
     * @param pageable pagination information
     * @return filtered paginated list of products
     */
    Page<Product> findByTitleContainingAndCategory_CategoryIdAndPriceBetween(
            String title,
            String categoryId,
            Double minPrice,
            Double maxPrice,
            Pageable pageable
    );

    /**
     * Search products by title and category.
     *
     * @param title product title keyword
     * @param categoryId category ID
     * @param pageable pagination information
     * @return filtered paginated list of products
     */
    Page<Product> findByTitleContainingAndCategory_CategoryId(
            String title,
            String categoryId,
            Pageable pageable
    );

    /**
     * Search products by title and price range.
     *
     * @param title product title keyword
     * @param minPrice minimum price
     * @param maxPrice maximum price
     * @param pageable pagination information
     * @return filtered paginated list of products
     */
    Page<Product> findByTitleContainingAndPriceBetween(
            String title,
            Double minPrice,
            Double maxPrice,
            Pageable pageable
    );

}