package com.Cloudy.Cloudy_Self_Checkout_POS.service;

import com.Cloudy.Cloudy_Self_Checkout_POS.customException.CustomSystemException;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.CheckoutDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface CheckoutService {
    ResponseEntity<ApiResponseWrapper<CheckoutDTO>> initiatePayment(CheckoutDTO request, String  username);
    ResponseEntity<ApiResponseWrapper<CheckoutDTO>> executePayment(CheckoutDTO request, String paymentId, String payerId,String username);
    ResponseEntity<ApiResponseWrapper<String>> cancelPurchase(Long purchaseId);
    Integer getLoyaltyPoints(String username) throws CustomSystemException;



}
