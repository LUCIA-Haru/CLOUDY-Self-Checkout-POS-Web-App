package com.Cloudy.Cloudy_Self_Checkout_POS.service;

import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.Discount;
import com.Cloudy.Cloudy_Self_Checkout_POS.customException.CustomSystemException;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.DiscountBarcodeDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.DiscountDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.PaginatedResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface DiscountService {
    ResponseEntity<ApiResponseWrapper<DiscountDTO>> addDiscount(DiscountDTO discountDTO, String username);
    ResponseEntity<ApiResponseWrapper<DiscountDTO>> updateDiscount(DiscountDTO discountDTO, String username, Long productId);
    ResponseEntity<ApiResponseWrapper<String>> deleteDiscount( String username, Long discountId);
    ResponseEntity<ApiResponseWrapper<List<DiscountBarcodeDTO>>> getActiveDiscounts(List<String> barcodes) throws CustomSystemException;
    ResponseEntity<ApiResponseWrapper<PaginatedResponse<DiscountDTO>>> getAllDiscounts(
            String username, String filterValue, int page, int size) throws CustomSystemException;
}
