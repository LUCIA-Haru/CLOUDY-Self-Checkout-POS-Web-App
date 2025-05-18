package com.Cloudy.Cloudy_Self_Checkout_POS.restImpl;

import com.Cloudy.Cloudy_Self_Checkout_POS.constants.ValueConstant;
import com.Cloudy.Cloudy_Self_Checkout_POS.customException.CustomSystemException;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.CouponAssignDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.CouponDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.rest.CouponRest;
import com.Cloudy.Cloudy_Self_Checkout_POS.service.CouponService;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class CouponRestImpl implements CouponRest {
    @Autowired
    CouponService couponService;

    private static final String couponsRestImpl = "CouponRestImpl";

    @Override
    public ResponseEntity<ApiResponseWrapper<CouponDTO>> generateCoupon(UserDetails userDetails, CouponDTO request) throws CustomSystemException {
        try{
            return  couponService.generateCoupon(userDetails.getUsername(),request);
        }catch (Exception e){
            log.error("{}=>{}=> Error:::: {}",couponsRestImpl,"generateCoupon()",e.getMessage());
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<BigDecimal>> useCoupon(UserDetails userDetails, Map<String, String> request) throws CustomSystemException {
         try{
            return  couponService.useCoupon(userDetails.getUsername(),request);
        }catch (Exception e){
            log.error("{}=>{}=> Error:::: {}",couponsRestImpl,"useCoupon()",e.getMessage());
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<String>> assignCoupon(UserDetails userDetails, Long cusId, String CouponCode) throws CustomSystemException {
        try{
            return  couponService.assignCoupon(userDetails.getUsername(),cusId,CouponCode);
        }catch (Exception e){
            log.error("{}=>{}=> Error:::: {}",couponsRestImpl,"useCoupon()",e.getMessage());
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<List<CouponDTO>>> fetchCoupons(UserDetails userDetails) throws CustomSystemException {
        try{
            return  couponService.fetchCoupons(userDetails.getUsername());
        }catch (Exception e){
            log.error("{}=>{}=> Error:::: {}",couponsRestImpl,"fetchCoupons()",e.getMessage());
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<Map<String, Object>>> getAllCouponsAssignments(UserDetails userDetails) throws CustomSystemException {
        try{
            return  couponService.getAllCouponsAssignments(userDetails.getUsername());
        }catch (Exception e){
            log.error("{}=>{}=> Error:::: {}",couponsRestImpl,"getAllCouponsAssignments()",e.getMessage());
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<CouponDTO>> updateCoupon(UserDetails userDetails, CouponDTO request, Long couponId) throws CustomSystemException {
        try{
            return  couponService.updateCoupon(userDetails.getUsername(),request,couponId);
        }catch (Exception e){
            log.error("{}=>{}=> Error:::: {}",couponsRestImpl,"updateCoupon()",e.getMessage());
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<String>> deleteCoupon(UserDetails userDetails, Long couponId) throws CustomSystemException {
        try{
            return  couponService.deleteCoupon(userDetails.getUsername(),couponId);
        }catch (Exception e){
            log.error("{}=>{}=> Error:::: {}",couponsRestImpl,"deleteCoupon()",e.getMessage());
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<List<CouponDTO>>> getAllCoupons(UserDetails userDetails) throws CustomSystemException {
        try {
            return couponService.getAllCoupons(userDetails.getUsername());
        } catch (Exception e) {
            log.error("{}=>{}=> Error:::: {}", couponsRestImpl, "getAllCoupons()", e.getMessage());
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }
}
