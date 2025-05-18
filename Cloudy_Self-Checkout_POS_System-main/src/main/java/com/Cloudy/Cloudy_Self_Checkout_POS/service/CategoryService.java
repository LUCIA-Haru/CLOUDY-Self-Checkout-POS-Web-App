package com.Cloudy.Cloudy_Self_Checkout_POS.service;

import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.Category;
import com.Cloudy.Cloudy_Self_Checkout_POS.customException.CustomSystemException;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.CategoryRequestBody;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.PaginatedResponse;
import org.checkerframework.checker.units.qual.A;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

public interface CategoryService {

    ResponseEntity<ApiResponseWrapper<CategoryRequestBody>> addCategory(UserDetails userDetails, CategoryRequestBody requestBody);

    ResponseEntity<ApiResponseWrapper<PaginatedResponse<Category>>> getAllCategory(String filterValue , int max, int min) throws CustomSystemException;

    ResponseEntity<ApiResponseWrapper<Category>> getCategoryById(Long id) throws CustomSystemException;

    ResponseEntity<ApiResponseWrapper<Category>> updateCategoryByID(UserDetails userDetails,Long id, CategoryRequestBody requestBody) throws CustomSystemException;

    ResponseEntity<ApiResponseWrapper<String>> deleteCategory(String username,Long id);
}
