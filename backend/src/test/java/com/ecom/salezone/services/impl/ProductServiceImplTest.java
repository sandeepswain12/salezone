package com.ecom.salezone.services.impl;

import com.ecom.salezone.dtos.ProductDto;
import com.ecom.salezone.enities.Category;
import com.ecom.salezone.enities.Product;
import com.ecom.salezone.exceptions.ResourceNotFoundException;
import com.ecom.salezone.repository.CategoryRepository;
import com.ecom.salezone.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ModelMapper mapper;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void createWithCategory_shouldAttachCategoryAndReturnSavedProduct() {
        Category category = new Category();
        category.setCategoryId("cat-1");
        ProductDto request = ProductDto.builder().title("Laptop").build();
        Product mappedProduct = new Product();
        Product savedProduct = new Product();
        savedProduct.setProductId("product-1");
        ProductDto response = ProductDto.builder().productId("product-1").title("Laptop").build();

        when(categoryRepository.findById("cat-1")).thenReturn(Optional.of(category));
        when(mapper.map(request, Product.class)).thenReturn(mappedProduct);
        when(productRepository.save(mappedProduct)).thenReturn(savedProduct);
        when(mapper.map(savedProduct, ProductDto.class)).thenReturn(response);

        ProductDto result = productService.createWithCategory(request, "cat-1", "log-1");

        assertNotNull(mappedProduct.getProductId());
        assertEquals(category, mappedProduct.getCategory());
        assertEquals("product-1", result.getProductId());
    }

    @Test
    void get_shouldThrowWhenProductIsMissing() {
        when(productRepository.findById("missing")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> productService.get("missing", "log-1")
        );

        assertEquals("Product not found of given Id !!", exception.getMessage());
    }

    @Test
    void delete_shouldDeleteImageFileWhenProductExists() throws Exception {
        Path tempDir = Files.createTempDirectory("salezone-product-test");
        Path imageFile = Files.createFile(tempDir.resolve("product.png"));

        Product product = new Product();
        product.setProductId("product-1");
        product.setProductImageName("product.png");

        ReflectionTestUtils.setField(productService, "imagePath", tempDir.toString() + "\\");
        when(productRepository.findById("product-1")).thenReturn(Optional.of(product));

        productService.delete("product-1", "log-1");

        verify(productRepository).delete(product);
        assertEquals(false, Files.exists(imageFile));
    }
}
