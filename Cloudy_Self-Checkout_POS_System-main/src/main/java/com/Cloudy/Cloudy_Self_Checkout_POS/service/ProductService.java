package com.Cloudy.Cloudy_Self_Checkout_POS.service;

import com.Cloudy.Cloudy_Self_Checkout_POS.customException.CustomSystemException;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.DiscountedProductDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.ProductDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ListWrapper;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.PaginatedResponse;
import org.springframework.http.ResponseEntity;

public interface ProductService{
    ResponseEntity<ApiResponseWrapper<ProductDTO>> addProduct(String username, ProductDTO requestBody);

    ResponseEntity<ApiResponseWrapper<ProductDTO>> findByBarcode(String barcode);

    ResponseEntity<ApiResponseWrapper<Boolean>> validateStock(String barcode,Integer quantity);

    ResponseEntity<ApiResponseWrapper<ProductDTO>> getProductById(String username, Long id);

    ResponseEntity<ApiResponseWrapper<PaginatedResponse<ProductDTO>>> getAllProducts( String filterValue, int page, int size) throws CustomSystemException;

    ResponseEntity<ApiResponseWrapper<ProductDTO>> updateProduct(String username, Long id, ProductDTO dto);

    ResponseEntity<ApiResponseWrapper<String>> deleteProduct(String username, Long id);

    ResponseEntity<ApiResponseWrapper<ListWrapper<DiscountedProductDTO>>> getsProductsOnDiscount();
}
