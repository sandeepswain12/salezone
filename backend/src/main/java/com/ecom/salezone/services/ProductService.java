package com.ecom.salezone.services;

import com.ecom.salezone.dtos.PageableResponse;
import com.ecom.salezone.dtos.ProductDto;

import java.util.List;

public interface ProductService {

    //create
    ProductDto create(ProductDto productDto,String logkey);

    //Bulk create
    List<ProductDto> createBulk(List<ProductDto> productDtos, String logkey);

    //update
    ProductDto update(ProductDto productDto, String productId,String logkey);

    //delete
    void delete(String productId,String logkey);

    //get single

    ProductDto get(String productId,String logkey);

    //get all
    PageableResponse<ProductDto> getAll(int pageNumber, int pageSize, String sortBy, String sortDir , String logkey);

    //get all : live
    PageableResponse<ProductDto> getAllLive(int pageNumber, int pageSize, String sortBy, String sortDir,String logkey);

    //search product
//    PageableResponse<ProductDto> searchByTitle(String subTitle, int pageNumber, int pageSize, String sortBy, String sortDir,String logkey);
    PageableResponse<ProductDto> searchProducts(
            String query,
            String categoryId,
            Double minPrice,
            Double maxPrice,
            int pageNumber,
            int pageSize,
            String sortBy,
            String sortDir,
            String logkey);


    //create product with category
    ProductDto createWithCategory(ProductDto productDto,String categoryId,String logkey);


    //update category of product
    ProductDto updateCategory(String productId,String categoryId,String logkey);

    PageableResponse<ProductDto> getAllOfCategory(String categoryId,int pageNumber,int pageSize,String sortBy, String sortDir,String logkey);

    //other methods


}