package com.ecom.salezone.services.impl;

import com.ecom.salezone.dtos.PageableResponse;
import com.ecom.salezone.dtos.ProductDto;
import com.ecom.salezone.enities.Category;
import com.ecom.salezone.enities.Product;
import com.ecom.salezone.exceptions.ResourceNotFoundException;
import com.ecom.salezone.util.Helper;
import com.ecom.salezone.repository.CategoryRepository;
import com.ecom.salezone.repository.ProductRepository;
import com.ecom.salezone.services.ProductService;
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
public class ProductServiceImpl implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private CategoryRepository categoryRepository;

    // ================= CREATE PRODUCT =================
    @Override
    public ProductDto create(ProductDto productDto,String logkey) {

        log.info("{} Creating product with title: {}",logkey, productDto.getTitle());

        Product product = mapper.map(productDto, Product.class);

        String productId = UUID.randomUUID().toString();
        product.setProductId(productId);

        Product saveProduct = productRepository.save(product);

        log.info("{} Product created successfully with ID: {}",logkey, productId);

        return mapper.map(saveProduct, ProductDto.class);
    }

    // ================= UPDATE PRODUCT =================
    @Override
    public ProductDto update(ProductDto productDto, String productId,String logkey) {

        log.info("{} Updating product with ID: {}",logkey, productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("{} Product not found for update. ID: {}",logkey, productId);
                    return new ResourceNotFoundException("Product not found of given Id !!");
                });

        product.setTitle(productDto.getTitle());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setDiscountedPrice(productDto.getDiscountedPrice());
        product.setQuantity(productDto.getQuantity());
        product.setLive(productDto.isLive());
        product.setStock(productDto.isStock());
        product.setProductImageName(productDto.getProductImageName());

        Product updatedProduct = productRepository.save(product);

        log.info("{} Product updated successfully with ID: {}",logkey, productId);

        return mapper.map(updatedProduct, ProductDto.class);
    }

    // ================= DELETE PRODUCT =================
    @Override
    public void delete(String productId,String logkey) {

        log.warn("{} Deleting product with ID: {}",logkey, productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("{} Product not found for delete. ID: {}",logkey, productId);
                    return new ResourceNotFoundException("Product not found of given Id !!");
                });

        productRepository.delete(product);

        log.info("{} Product deleted successfully with ID: {}",logkey, productId);
    }

    // ================= GET PRODUCT =================
    @Override
    public ProductDto get(String productId,String logkey) {

        log.debug("{} Fetching product with ID: {}",logkey, productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("{} Product not found. ID: {}",logkey, productId);
                    return new ResourceNotFoundException("Product not found of given Id !!");
                });

        log.info("{} Product fetched successfully with ID: {}",logkey, productId);

        return mapper.map(product, ProductDto.class);
    }

    // ================= GET ALL PRODUCTS =================
    @Override
    public PageableResponse<ProductDto> getAll(int pageNumber, int pageSize, String sortBy, String sortDir , String logkey) {



        log.debug("{} Fetching all products | page={} size={} sortBy={} sortDir={}",
                logkey, pageNumber, pageSize, sortBy, sortDir);

        Sort sort = (sortDir.equalsIgnoreCase("desc"))
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Product> page = productRepository.findAll(pageable);

        log.info("{} Total products fetched: {}", logkey, page.getNumberOfElements());

        return Helper.getPageableResponse(page, ProductDto.class, logkey);
    }

    // ================= GET ALL LIVE PRODUCTS =================
    @Override
    public PageableResponse<ProductDto> getAllLive(int pageNumber, int pageSize, String sortBy, String sortDir,String logkey) {

        log.debug("{} Fetching live products | page={} size={} sortBy={} sortDir={}",
                logkey, pageNumber, pageSize, sortBy, sortDir);

        Sort sort = (sortDir.equalsIgnoreCase("desc"))
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Product> page = productRepository.findByLiveTrue(pageable);

        log.info("{} Live products fetched: {}", logkey, page.getNumberOfElements());

        return Helper.getPageableResponse(page, ProductDto.class, logkey);
    }

    // ================= SEARCH PRODUCT =================
    @Override
    public PageableResponse<ProductDto> searchByTitle(String subTitle, int pageNumber, int pageSize, String sortBy, String sortDir,String logkey) {

        log.debug("{} Searching products with title '{}' | page={} size={}",
                logkey, subTitle, pageNumber, pageSize);

        Sort sort = (sortDir.equalsIgnoreCase("desc"))
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Product> page = productRepository.findByTitleContaining(subTitle, pageable);

        log.info("{} Search results count: {}", logkey, page.getNumberOfElements());

        return Helper.getPageableResponse(page, ProductDto.class, logkey);
    }

    // ================= CREATE PRODUCT WITH CATEGORY =================
    @Override
    public ProductDto createWithCategory(ProductDto productDto, String categoryId,String logkey) {

        log.info("{} Creating product with category ID: {}",logkey, categoryId);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.error("{} Category not found. ID: {}",logkey, categoryId);
                    return new ResourceNotFoundException("Category not found !!");
                });

        Product product = mapper.map(productDto, Product.class);

        String productId = UUID.randomUUID().toString();
        product.setProductId(productId);
        product.setCategory(category);

        Product saveProduct = productRepository.save(product);

        log.info("{} Product created with ID: {} under category ID: {}",logkey, productId, categoryId);

        return mapper.map(saveProduct, ProductDto.class);
    }

    // ================= UPDATE PRODUCT CATEGORY =================
    @Override
    public ProductDto updateCategory(String productId, String categoryId,String logkey) {

        log.info("{} Updating category for product ID: {}",logkey, productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("{} Product not found. ID: {}",logkey, productId);
                    return new ResourceNotFoundException("Product of given id not found !!");
                });

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.error("{} Category not found. ID: {}",logkey, categoryId);
                    return new ResourceNotFoundException("Category of given id not found !!");
                });

        product.setCategory(category);
        Product savedProduct = productRepository.save(product);

        log.info("{}Category updated successfully for product ID: {}",logkey, productId);

        return mapper.map(savedProduct, ProductDto.class);
    }

    // ================= GET PRODUCTS BY CATEGORY =================
    @Override
    public PageableResponse<ProductDto> getAllOfCategory(String categoryId, int pageNumber, int pageSize, String sortBy, String sortDir,String logkey) {



        log.debug("{} Fetching products for category ID: {}", logkey, categoryId);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.error("{} Category not found. ID: {}", logkey, categoryId);
                    return new ResourceNotFoundException("Category of given id not found !!");
                });

        Sort sort = (sortDir.equalsIgnoreCase("desc"))
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Product> page = productRepository.findByCategory(category, pageable);

        log.info("{} Products fetched for category {} : {}",
                logkey, categoryId, page.getNumberOfElements());

        return Helper.getPageableResponse(page, ProductDto.class, logkey);
    }
}
