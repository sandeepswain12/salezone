package com.ecom.salezone.services.impl;

import com.ecom.salezone.dtos.CategoryDto;
import com.ecom.salezone.dtos.PageableResponse;
import com.ecom.salezone.enities.Category;
import com.ecom.salezone.exceptions.ResourceNotFoundException;
import com.ecom.salezone.helper.Helper;
import com.ecom.salezone.helper.LogKeyGenerator;
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

import java.util.UUID;

@Service
public class CategoryServiceImpl implements CategoryService {

    private static final Logger log = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    // ================= CREATE CATEGORY =================
    @Override
    public CategoryDto create(CategoryDto categoryDto) {

        log.info("Creating category with title: {}", categoryDto.getTitle());

        String categoryId = UUID.randomUUID().toString();
        categoryDto.setCategoryId(categoryId);

        Category category = dtoToEntity(categoryDto);
        Category savedCategory = categoryRepository.save(category);

        log.info("Category created successfully with ID: {}", categoryId);

        return entityToDto(savedCategory);
    }

    // ================= UPDATE CATEGORY =================
    @Override
    public CategoryDto update(CategoryDto categoryDto, String categoryId) {

        log.info("Updating category with ID: {}", categoryId);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.error("Category not found for update. ID: {}", categoryId);
                    return new ResourceNotFoundException("Category not found with given id !!");
                });

        category.setTitle(categoryDto.getTitle());
        category.setDescription(categoryDto.getDescription());
        category.setCoverImage(categoryDto.getCoverImage());

        Category updatedCategory = categoryRepository.save(category);

        log.info("Category updated successfully with ID: {}", categoryId);

        return entityToDto(updatedCategory);
    }

    // ================= DELETE CATEGORY =================
    @Override
    public void delete(String categoryId) {

        log.warn("Deleting category with ID: {}", categoryId);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.error("Category not found for delete. ID: {}", categoryId);
                    return new ResourceNotFoundException("Category not found with given id !!");
                });

        categoryRepository.delete(category);

        log.info("Category deleted successfully with ID: {}", categoryId);
    }

    // ================= GET ALL CATEGORIES =================
    @Override
    public PageableResponse<CategoryDto> getAll(
            int pageNumber,
            int pageSize,
            String sortBy,
            String sortDir) {

        String logkey = LogKeyGenerator.generateLogKey();

        log.debug("[{}] Fetching all categories | page={} size={} sortBy={} sortDir={}",
                logkey, pageNumber, pageSize, sortBy, sortDir);

        Sort sort = (sortDir.equalsIgnoreCase("desc"))
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Category> page = categoryRepository.findAll(pageable);

        log.info("[{}] Categories fetched count: {}", logkey, page.getNumberOfElements());

        PageableResponse<CategoryDto> pageableResponse =
                Helper.getPageableResponse(page, CategoryDto.class, logkey);

        return pageableResponse;
    }

    // ================= GET SINGLE CATEGORY =================
    @Override
    public CategoryDto get(String categoryId) {

        log.debug("Fetching category with ID: {}", categoryId);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.error("Category not found. ID: {}", categoryId);
                    return new ResourceNotFoundException("Category not found with given id !!");
                });

        log.info("Category fetched successfully with ID: {}", categoryId);

        return entityToDto(category);
    }

    // ================= MAPPER METHODS =================
    public Category dtoToEntity(CategoryDto categoryDto) {
        log.debug("Mapping CategoryDto to Category entity");
        return modelMapper.map(categoryDto, Category.class);
    }

    public CategoryDto entityToDto(Category category) {
        log.debug("Mapping Category entity to CategoryDto");
        return modelMapper.map(category, CategoryDto.class);
    }
}
