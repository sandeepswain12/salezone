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

        log.info("LogKey: {} - Entry into create method with payload = {}", logkey, productDto);

        Product product = mapper.map(productDto, Product.class);

        String productId = UUID.randomUUID().toString();
        log.info("LogKey: {} - Product id generated | productId = {}", logkey, productId);
        product.setProductId(productId);

        Product saveProduct = productRepository.save(product);
        log.info("LogKey: {} - Product saved in DB id : {}",logkey, productId);

        return mapper.map(saveProduct, ProductDto.class);
    }

    // ================= UPDATE PRODUCT =================
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

        Product updatedProduct = productRepository.save(product);
        log.info("LogKey: {} - Product updated in DB product Id: {}",logkey, productId);

        return mapper.map(updatedProduct, ProductDto.class);
    }

    // ================= DELETE PRODUCT =================
    @Override
    public void delete(String productId,String logkey) {

        log.warn("LogKey: {} - Entry into delete method with productId: {}",logkey, productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("LogKey: {} - Product not found for delete. ID: {}",logkey, productId);
                    return new ResourceNotFoundException("Product not found of given Id !!");
                });
        log.info("LogKey: {} - Fetched product form DB | product = {}", logkey, product);

        productRepository.delete(product);
        log.info("LogKey: {} - Product deleted from DB | product = {}", logkey, product);
    }

    // ================= GET PRODUCT =================
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

    // ================= GET ALL PRODUCTS =================
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

    // ================= GET ALL LIVE PRODUCTS =================
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

    // ================= SEARCH PRODUCT =================
    @Override
    public PageableResponse<ProductDto> searchByTitle(String subTitle, int pageNumber, int pageSize, String sortBy, String sortDir,String logkey) {

        log.info("LogKey: {} Entry into searchByTitle method | page={} size={} sortBy={} sortDir={}",
                logkey, pageNumber, pageSize, sortBy, sortDir);

        Sort sort = (sortDir.equalsIgnoreCase("desc"))
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        log.debug("LogKey: {} - Products sorted | sortedResult = {}", logkey, sort);

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        log.debug("LogKey: {} - Created a pageable obj | sortedResult = {}", logkey, pageable);

        Page<Product> page = productRepository.findByTitleContaining(subTitle, pageable);
        log.info("LogKey: {} - Search by tile Products fetched form DB | Total products = {}", logkey,page.getNumberOfElements());

        return Helper.getPageableResponse(page, ProductDto.class, logkey);
    }

    // ================= CREATE PRODUCT WITH CATEGORY =================
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

    // ================= UPDATE PRODUCT CATEGORY =================
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

    // ================= GET PRODUCTS BY CATEGORY =================
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
