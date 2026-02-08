package com.ecom.salezone.services;

import com.ecom.salezone.dtos.CategoryDto;
import com.ecom.salezone.dtos.PageableResponse;

public interface CategoryService
{

    //create
    CategoryDto create(CategoryDto categoryDto, String logkey);

    //update
    CategoryDto update(CategoryDto categoryDto, String categoryId, String logkey);

    //delete

    void delete(String categoryId, String logkey);

    //get all
    PageableResponse<CategoryDto> getAll(int pageNumber, int pageSize, String sortBy, String sortDir, String logkey);

    //get single category detail
    CategoryDto get(String categoryId, String logkey);

    //search:
}
