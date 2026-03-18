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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of ProductService for the SaleZone E-commerce system.
 *
 * Handles all product related business logic including:
 * - Creating and updating products
 * - Bulk product creation
 * - Deleting products and associated images
 * - Fetching products with pagination and sorting
 * - Searching products using filters (category, price, keyword)
 * - Managing product category assignments
 *
 * Integrates caching to optimize product retrieval operations.
 *
 * @author Sandeep Kumar Swain
 * @version 1.0
 * @since 15-03-2026
 */
@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @Value("${product.image.path}")
    private String imagePath;

    /* Create Product */
    @CacheEvict(
            value = {"products","live_products","search_products","category_products"},
            condition = "@cacheFlags.productCacheEnabled()",
            allEntries = true
    )
    @Override
    public ProductDto create(ProductDto productDto,String logkey) {

        log.info("LogKey: {} - Entry into create method with payload = {}", logkey, productDto);

        Product product = mapper.map(productDto, Product.class);

        String productId = UUID.randomUUID().toString();
        log.info("LogKey: {} - Product id generated | productId = {}", logkey, productId);
        product.setProductId(productId);

        Product saveProduct = productRepository.save(product);
        log.info("LogKey: {} - Product saved in DB id : {}",logkey, productId);

        return mapper.map(saveProduct, ProductDto.class);
    }


    /* Create Bulk Product */
    @Override
    public List<ProductDto> createBulk(List<ProductDto> productDtos, String logkey) {

        log.info("LogKey: {} - Entry into createBulk method | totalProducts={}",
                logkey, productDtos.size());

        List<Product> products = productDtos.stream().map(dto -> {

            Product product = mapper.map(dto, Product.class);

            String productId = UUID.randomUUID().toString();
            product.setProductId(productId);

            log.info("LogKey: {} - Product id generated | productId={}",
                    logkey, productId);

            return product;

        }).toList();

        List<Product> savedProducts = productRepository.saveAll(products);

        log.info("LogKey: {} - All products saved successfully | count={}",
                logkey, savedProducts.size());

        return savedProducts.stream()
                .map(product -> mapper.map(product, ProductDto.class))
                .toList();
    }

    /* Update Product */
    @CacheEvict(
            value = {"products","live_products","search_products","category_products"},
            condition = "@cacheFlags.productCacheEnabled()",
            allEntries = true
    )
    @Override
    public ProductDto update(ProductDto productDto, String productId,String logkey) {

        log.info("LogKey: {} - Entry into update method | payload = {}", logkey, productDto);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("LogKey: {} - Product not found for update. ID: {}",logkey, productId);
                    return new ResourceNotFoundException("Product not found of given Id !!");
                });
        log.info("LogKey: {} - Fetched existing product form DB | existing product = {}", logkey, product);

        product.setTitle(productDto.getTitle());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setDiscountedPrice(productDto.getDiscountedPrice());
        product.setQuantity(productDto.getQuantity());
        product.setLive(productDto.isLive());
        product.setStock(productDto.isStock());
        product.setProductImageName(productDto.getProductImageName());
        product.setProductImageUrl(productDto.getProductImageUrl());

        Product updatedProduct = productRepository.save(product);
        log.info("LogKey: {} - Product updated in DB product Id: {}",logkey, productId);

        return mapper.map(updatedProduct, ProductDto.class);
    }

    /* Delete Product */
    @CacheEvict(
            value = {"products","live_products","search_products","category_products"},
            condition = "@cacheFlags.productCacheEnabled()",
            allEntries = true
    )
    @Override
    public void delete(String productId,String logkey) {

        log.warn("LogKey: {} - Entry into delete method with productId: {}",logkey, productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("LogKey: {} - Product not found for delete. ID: {}",logkey, productId);
                    return new ResourceNotFoundException("Product not found of given Id !!");
                });
        log.info("LogKey: {} - Fetched product form DB | product = {}", logkey, product);

        String fullPath = imagePath + product.getProductImageName();

        try {
            Files.delete(Paths.get(fullPath));
            log.info("LogKey: {} - Product image deleted | path={}", logkey, fullPath);
        } catch (NoSuchFileException ex) {
            log.warn("LogKey: {} - Product image not found | path={}", logkey, fullPath);
        } catch (IOException e) {
            log.error("LogKey: {} - Error deleting product image | path={}", logkey, fullPath, e);
        }

        productRepository.delete(product);
        log.info("LogKey: {} - Product deleted from DB | product = {}", logkey, product);
    }

    /* Get Product */
    @Cacheable(
            value = "products",
            condition = "@cacheFlags.productCacheEnabled()",
            key = "#productId"
    )
    @Override
    public ProductDto get(String productId,String logkey) {

        log.info("LogKey: {} - Entry into getProductById with id = {}", logkey, productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("LogKey: {} - Product not found. ID: {}",logkey, productId);
                    return new ResourceNotFoundException("Product not found of given Id !!");
                });
        log.info("LogKey: {} - Product fetched form DB | product = {}", logkey, product);

        return mapper.map(product, ProductDto.class);
    }

    /* Get All Products */
    @Cacheable(
            value = "products",
            key = "'page_' + #pageNumber + '_size_' + #pageSize + '_sort_' + #sortBy + '_' + #sortDir",
            condition = "@cacheFlags.productCacheEnabled()"
    )
    @Override
    public PageableResponse<ProductDto> getAll(int pageNumber, int pageSize, String sortBy, String sortDir , String logkey) {

        log.info("LogKey: {} - Entry into getAllProducts | page={} size={} sortBy={} sortDir={}",
                logkey, pageNumber, pageSize, sortBy, sortDir);

        Sort sort = (sortDir.equalsIgnoreCase("desc"))
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        log.debug("LogKey: {} - Products sorted as per sortDirection | sortedResult = {}", logkey, sort);

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        log.debug("LogKey: {} - Created a pageable object for response in page | sortedResult = {}", logkey, pageable);

        Page<Product> page = productRepository.findAll(pageable);
        log.info("LogKey: {} - Products fetched form DB | Total products = {}", logkey,page.getNumberOfElements());

        return Helper.getPageableResponse(page, ProductDto.class, logkey);
    }

    /* Get All Live Products */
    @Cacheable(
            value = "live_products",
            key = "'page_' + #pageNumber + '_size_' + #pageSize",
            condition = "@cacheFlags.productCacheEnabled()"
    )
    @Override
    public PageableResponse<ProductDto> getAllLive(int pageNumber, int pageSize, String sortBy, String sortDir,String logkey) {

        log.info("LogKey: {} Entry into getAllLive method | page={} size={} sortBy={} sortDir={}",
                logkey, pageNumber, pageSize, sortBy, sortDir);

        Sort sort = (sortDir.equalsIgnoreCase("desc"))
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        log.debug("LogKey: {} - Products sorted as per sortDir | sortedResult = {}", logkey, sort);

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        log.debug("LogKey: {} - Created a pageable obj for response in page | sortedResult = {}", logkey, pageable);

        Page<Product> page = productRepository.findByLiveTrue(pageable);
        log.info("LogKey: {} - All live Products fetched form DB | Total products = {}", logkey,page.getNumberOfElements());

        return Helper.getPageableResponse(page, ProductDto.class, logkey);
    }

    /* Search Products */
    @Cacheable(
            value = "search_products",
            key = "#query + '_' + #categoryId + '_' + #minPrice + '_' + #maxPrice + '_' + #pageNumber",
            condition = "@cacheFlags.productCacheEnabled()"
    )
    @Override
    public PageableResponse<ProductDto> searchProducts(
            String query,
            String categoryId,
            Double minPrice,
            Double maxPrice,
            int pageNumber,
            int pageSize,
            String sortBy,
            String sortDir,
            String logkey) {

        log.info("LogKey: {} - Entry into searchProducts", logkey);

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<Product> page;

        // CASE 1: Category + Price
        if (categoryId != null && minPrice != null && maxPrice != null) {

            page = productRepository
                    .findByTitleContainingAndCategory_CategoryIdAndPriceBetween(
                            query, categoryId, minPrice, maxPrice, pageable);

        }

        // CASE 2: Only Category
        else if (categoryId != null) {

            page = productRepository
                    .findByTitleContainingAndCategory_CategoryId(
                            query, categoryId, pageable);

        }

        // CASE 3: Only Price
        else if (minPrice != null && maxPrice != null) {

            page = productRepository
                    .findByTitleContainingAndPriceBetween(
                            query, minPrice, maxPrice, pageable);

        }

        // CASE 4: Default
        else {

            page = productRepository
                    .findByTitleContaining(query, pageable);

        }

        log.info("LogKey: {} - Products fetched from DB | count={}",
                logkey, page.getNumberOfElements());

        return Helper.getPageableResponse(page, ProductDto.class, logkey);
    }

    /* Create Product With Category */
    @CacheEvict(value = {"products","live_products","search_products","category_products"},
            condition = "@cacheFlags.productCacheEnabled()",
            allEntries = true
    )
    @Override
    public ProductDto createWithCategory(ProductDto productDto, String categoryId,String logkey) {

        log.info("LogKey: {} - Entry into Creating product with category | categoryId: {}",logkey, categoryId);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.error("LogKey: {} - Category not found. ID: {}",logkey, categoryId);
                    return new ResourceNotFoundException("Category not found !!");
                });
        log.info("LogKey: {} - Fetched category from DB | categoryId: {}",logkey, categoryId);

        Product product = mapper.map(productDto, Product.class);

        String productId = UUID.randomUUID().toString();
        log.info("LogKey: {} - Product id generated | productId: {}",logkey, productId);
        product.setProductId(productId);
        product.setCategory(category);

        Product saveProduct = productRepository.save(product);
        log.info("LogKey: {} - Product saved in DB | productId{} categoryID: {}",logkey, productId, categoryId);

        return mapper.map(saveProduct, ProductDto.class);
    }

    /* Update Product Category */
    @CacheEvict(value = {"products","live_products","search_products","category_products"},
            condition = "@cacheFlags.productCacheEnabled()",
            allEntries = true
    )
    @Override
    public ProductDto updateCategory(String productId, String categoryId, String logkey) {

        log.info("LogKey: {} - Entry into updateCategory method | productId={} categoryId={}",
                logkey, productId, categoryId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("LogKey: {} - Product not found for category update. ID: {}",
                            logkey, productId);
                    return new ResourceNotFoundException("Product of given id not found !!");
                });

        log.info("LogKey: {} - Fetched product from DB | product = {}",
                logkey, product);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.error("LogKey: {} - Category not found for product update. ID: {}",
                            logkey, categoryId);
                    return new ResourceNotFoundException("Category of given id not found !!");
                });

        log.info("LogKey: {} - Fetched category from DB , category = {}",
                logkey, category);

        product.setCategory(category);

        Product savedProduct = productRepository.save(product);

        log.info("LogKey: {} - Product category updated successfully | productId={} categoryId={}",
                logkey, productId, categoryId);

        return mapper.map(savedProduct, ProductDto.class);
    }

    /* Get Products By Category */
    @Cacheable(
            value = "category_products",
            key = "#categoryId + '_' + #pageNumber",
            condition = "@cacheFlags.productCacheEnabled()"
    )
    @Override
    public PageableResponse<ProductDto> getAllOfCategory(String categoryId,
                                                         int pageNumber,
                                                         int pageSize,
                                                         String sortBy,
                                                         String sortDir,
                                                         String logkey) {

        log.info("LogKey: {} - Entry into getAllOfCategory method | categoryId={} page={} size={} sortBy={} sortDir={}",
                logkey, categoryId, pageNumber, pageSize, sortBy, sortDir);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.error("LogKey: {} - Category not found ID: {}",
                            logkey, categoryId);
                    return new ResourceNotFoundException("Category of given id not found !!");
                });

        log.info("LogKey: {} - Fetched category from DB | category = {}",
                logkey, category);

        Sort sort = (sortDir.equalsIgnoreCase("desc"))
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        log.debug("LogKey: {} - Products sorted as per sortDir , sortedResult = {}",
                logkey, sort);

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        log.debug("LogKey: {} - Created pageable object for category products | pageable = {}",
                logkey, pageable);

        Page<Product> page = productRepository.findByCategory(category, pageable);

        log.info("LogKey: {} - Products fetched from DB for category | categoryId={} totalProducts={}",
                logkey, categoryId, page.getNumberOfElements());

        return Helper.getPageableResponse(page, ProductDto.class, logkey);
    }
}
