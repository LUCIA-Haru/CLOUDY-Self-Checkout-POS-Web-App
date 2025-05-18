package com.Cloudy.Cloudy_Self_Checkout_POS.service;

import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.Supplier;
import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.SupplierTransaction;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.SupplierDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.SupplierTransactionDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.PaginatedResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SupplierService {
    ResponseEntity<ApiResponseWrapper<Supplier>> addSupplier(String username, SupplierDTO dto);

    ResponseEntity<ApiResponseWrapper<Supplier>> updateSupplier(String username, SupplierDTO dto);
    ResponseEntity<ApiResponseWrapper<String>> deleteSupplier(String username, Long id);
    ResponseEntity<ApiResponseWrapper<Supplier>> getSupplierById(String username, Long id);

    ResponseEntity<ApiResponseWrapper<PaginatedResponse<Supplier>>> getAllSuppliers(String username , String filterValue, int min, int max);
    ResponseEntity<ApiResponseWrapper<List<SupplierTransactionDTO>>> getSupplierTransaction(String username);
}
