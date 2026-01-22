package com.ecom.salezone.repository;

import com.ecom.salezone.dtos.CategoryDto;
import com.ecom.salezone.enities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, String> {
}
