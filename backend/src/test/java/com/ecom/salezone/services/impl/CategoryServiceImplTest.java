package com.ecom.salezone.services.impl;

import com.ecom.salezone.dtos.CategoryDto;
import com.ecom.salezone.enities.Category;
import com.ecom.salezone.exceptions.ResourceNotFoundException;
import com.ecom.salezone.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void create_shouldGenerateIdAndReturnSavedCategoryDto() {
        CategoryDto request = CategoryDto.builder()
                .title("Electronics")
                .description("Electronic items")
                .coverImage("cover.png")
                .build();
        Category mappedCategory = new Category();
        Category savedCategory = new Category();
        savedCategory.setCategoryId("cat-1");
        CategoryDto response = CategoryDto.builder().categoryId("cat-1").title("Electronics").build();

        when(modelMapper.map(any(CategoryDto.class), any(Class.class))).thenReturn(mappedCategory);
        when(categoryRepository.save(mappedCategory)).thenReturn(savedCategory);
        when(modelMapper.map(savedCategory, CategoryDto.class)).thenReturn(response);

        CategoryDto result = categoryService.create(request, "log-1");

        assertNotNull(request.getCategoryId());
        assertEquals("cat-1", result.getCategoryId());
    }

    @Test
    void get_shouldThrowWhenCategoryDoesNotExist() {
        when(categoryRepository.findById("missing")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.get("missing", "log-1")
        );

        assertEquals("Category not found with given id !!", exception.getMessage());
    }
}
