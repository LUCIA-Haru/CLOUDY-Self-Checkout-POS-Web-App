package com.Cloudy.Cloudy_Self_Checkout_POS.service;

import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.Brand;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.BrandDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.PaginatedResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BrandService {
    ResponseEntity<ApiResponseWrapper<BrandDTO>> addBrand(String username,BrandDTO brandDTO);

    ResponseEntity<ApiResponseWrapper<Brand>> updateBrand(String username,BrandDTO brandDTO);

    ResponseEntity<ApiResponseWrapper<String>> deleteBrand(String username, Long id);

    ResponseEntity<ApiResponseWrapper<Brand>> getBrandById(String username, Long id);

    ResponseEntity<ApiResponseWrapper<PaginatedResponse<Brand>>> getAllBrand( String filterValue, int min, int max);
}
