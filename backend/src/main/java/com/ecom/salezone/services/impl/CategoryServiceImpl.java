package com.ecom.salezone.services.impl;

import com.ecom.salezone.dtos.CategoryDto;
import com.ecom.salezone.dtos.PageableResponse;
import com.ecom.salezone.enities.Category;
import com.ecom.salezone.exceptions.ResourceNotFoundException;
import com.ecom.salezone.util.Helper;
import com.ecom.salezone.repository.CategoryRepository;
import com.ecom.salezone.services.CategoryService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.util.UUID;

@Service
public class CategoryServiceImpl implements CategoryService {

    private static final Logger log =
            LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    // ================= CREATE CATEGORY =================
    @CacheEvict(
            value = {"categories","categories_page"},
            condition = "@cacheFlags.categoryCacheEnabled()",
            allEntries = true
    )
    @Override
    public CategoryDto create(CategoryDto categoryDto, String logkey) {

        log.info("LogKey: {} - Entry into createCategory method | title={}",
                logkey, categoryDto.getTitle());

        String categoryId = UUID.randomUUID().toString();
        categoryDto.setCategoryId(categoryId);

        Category category = dtoToEntity(categoryDto);
        Category savedCategory = categoryRepository.save(category);

        log.info("LogKey: {} - Category created successfully | categoryId={}",
                logkey, categoryId);

        return entityToDto(savedCategory);
    }

    // ================= UPDATE CATEGORY =================
    @CacheEvict(
            value = {"categories","categories_page"},
            condition = "@cacheFlags.categoryCacheEnabled()",
            allEntries = true
    )
    @Override
    public CategoryDto update(CategoryDto categoryDto, String categoryId, String logkey) {

        log.info("LogKey: {} - Entry into updateCategory method | categoryId={}",
                logkey, categoryId);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.error("LogKey: {} - Category not found for update | categoryId={}",
                            logkey, categoryId);
                    return new ResourceNotFoundException("Category not found with given id !!");
                });

        category.setTitle(categoryDto.getTitle());
        category.setDescription(categoryDto.getDescription());
        category.setCoverImage(categoryDto.getCoverImage());

        Category updatedCategory = categoryRepository.save(category);

        log.info("LogKey: {} - Category updated successfully | categoryId={}",
                logkey, categoryId);

        return entityToDto(updatedCategory);
    }

    // ================= DELETE CATEGORY =================
    @CacheEvict(
            value = {"categories","categories_page"},
            condition = "@cacheFlags.categoryCacheEnabled()",
            allEntries = true
    )
    @Override
    public void delete(String categoryId, String logkey) {

        log.warn("LogKey: {} - Entry into deleteCategory method | categoryId={}",
                logkey, categoryId);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.error("LogKey: {} - Category not found for delete | categoryId={}",
                            logkey, categoryId);
                    return new ResourceNotFoundException("Category not found with given id !!");
                });

        categoryRepository.delete(category);

        log.info("LogKey: {} - Category deleted successfully | categoryId={}",
                logkey, categoryId);
    }

    // ================= GET ALL CATEGORIES =================
    @Cacheable(
            value = "categories_page",
            key = "'page_' + #pageNumber + '_size_' + #pageSize + '_sort_' + #sortBy + '_' + #sortDir",
            condition = "@cacheFlags.categoryCacheEnabled()"
    )
    @Override
    public PageableResponse<CategoryDto> getAll(
            int pageNumber,
            int pageSize,
            String sortBy,
            String sortDir,
            String logkey) {

        log.info("LogKey: {} - Entry into getAllCategories method | page={} size={} sortBy={} sortDir={}",
                logkey, pageNumber, pageSize, sortBy, sortDir);

        Sort sort = (sortDir.equalsIgnoreCase("desc"))
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Category> page = categoryRepository.findAll(pageable);

        log.info("LogKey: {} - Categories fetched from DB | count={}",
                logkey, page.getNumberOfElements());

        PageableResponse<CategoryDto> pageableResponse =
                Helper.getPageableResponse(page, CategoryDto.class, logkey);

        return pageableResponse;
    }

    // ================= GET SINGLE CATEGORY =================
    @Cacheable(
            value = "categories",
            key = "#categoryId",
            condition = "@cacheFlags.categoryCacheEnabled()"
    )
    @Override
    public CategoryDto get(String categoryId, String logkey) {

        log.info("LogKey: {} - Entry into getCategoryById method | categoryId={}",
                logkey, categoryId);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.error("LogKey: {} - Category not found | categoryId={}",
                            logkey, categoryId);
                    return new ResourceNotFoundException("Category not found with given id !!");
                });

        log.info("LogKey: {} - Category fetched successfully | categoryId={}",
                logkey, categoryId);

        return entityToDto(category);
    }

    // ================= MAPPER METHODS =================
    public Category dtoToEntity(CategoryDto categoryDto) {
        log.debug("LogKey: MAPPING - Converting CategoryDto to Category entity");
        return modelMapper.map(categoryDto, Category.class);
    }

    public CategoryDto entityToDto(Category category) {
        log.debug("LogKey: MAPPING - Converting Category entity to CategoryDto");
        return modelMapper.map(category, CategoryDto.class);
    }
}