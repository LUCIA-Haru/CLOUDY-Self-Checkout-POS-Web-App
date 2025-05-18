package com.Cloudy.Cloudy_Self_Checkout_POS.service;

import com.Cloudy.Cloudy_Self_Checkout_POS.customException.CustomSystemException;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.CouponDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface CouponService {
    ResponseEntity <ApiResponseWrapper<CouponDTO>>  generateCoupon(String username,CouponDTO couponDTO);
    ResponseEntity <ApiResponseWrapper<BigDecimal>> useCoupon(String username, Map<String,String> request) throws CustomSystemException;
    ResponseEntity <ApiResponseWrapper<String>> assignCoupon(String username,Long cusId,String CouponCode) throws CustomSystemException;
    ResponseEntity<ApiResponseWrapper<List<CouponDTO>>> fetchCoupons(String username) throws CustomSystemException;
    ResponseEntity<ApiResponseWrapper<Map<String,Object>>> getAllCouponsAssignments(String username) throws CustomSystemException;
    ResponseEntity<ApiResponseWrapper<CouponDTO>> updateCoupon(String username,CouponDTO couponDTO,Long couponId) throws CustomSystemException;
    ResponseEntity<ApiResponseWrapper<String>> deleteCoupon(String username,Long couponId) throws CustomSystemException;
    ResponseEntity<ApiResponseWrapper<List<CouponDTO>>> getAllCoupons(String username) throws CustomSystemException;
}
