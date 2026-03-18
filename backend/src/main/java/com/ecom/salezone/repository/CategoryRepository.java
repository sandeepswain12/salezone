package com.ecom.salezone.repository;

import com.ecom.salezone.enities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Category entities.
 *
 * Provides CRUD operations for product categories such as:
 * - Creating categories
 * - Updating categories
 * - Deleting categories
 * - Fetching category information
 *
 * This repository extends JpaRepository which automatically
 * provides common database operations.
 *
 * @author : Sandeep Kumar Swain
 * @since : 15-03-2026
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {

}