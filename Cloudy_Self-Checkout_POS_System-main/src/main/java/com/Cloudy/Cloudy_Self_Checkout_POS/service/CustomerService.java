package com.Cloudy.Cloudy_Self_Checkout_POS.service;

import com.Cloudy.Cloudy_Self_Checkout_POS.customException.CustomSystemException;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.CheckoutDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.CustomerRequestBody;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.PurchaseDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.CustomerWrapper;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CustomerService {
    ResponseEntity<ApiResponseWrapper<CustomerWrapper>> getCustomer(UserDetails userDetails);

    ResponseEntity<ApiResponseWrapper<CustomerWrapper>> updateCustomer(UserDetails userDetails ,CustomerRequestBody requestBody);

    ResponseEntity<ApiResponseWrapper<String>> uploadProfileImg(UserDetails userDetails, MultipartFile file);
    ResponseEntity<ApiResponseWrapper<Object>> generateVoucherPdf(String username, CheckoutDTO checkoutDTO);
    ResponseEntity<Resource> servePdf(String username, String filename) throws CustomSystemException;
    ResponseEntity<ApiResponseWrapper<Object>>getAllPdf(String username);
    ResponseEntity<ApiResponseWrapper<List<PurchaseDTO>>> getPurchaseHistory(String username) throws CustomSystemException;
}
