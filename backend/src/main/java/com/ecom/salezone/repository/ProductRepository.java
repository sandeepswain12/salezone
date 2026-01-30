package com.ecom.salezone.repository;

import com.ecom.salezone.dtos.CategoryDto;
import com.ecom.salezone.dtos.ProductDto;
import com.ecom.salezone.enities.Category;
import com.ecom.salezone.enities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Product entity.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    // Search products by title (contains)
    Page<Product> findByTitleContaining(String subTitle, Pageable pageable);

    // Fetch only live products
    Page<Product> findByLiveTrue(Pageable pageable);

    // Fetch products by category
    Page<Product> findByCategory(Category category, Pageable pageable);

    //other methods
    //custom finder methods
    //query methods

}
