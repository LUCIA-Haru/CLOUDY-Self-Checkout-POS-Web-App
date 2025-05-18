package com.Cloudy.Cloudy_Self_Checkout_POS.restImpl;

import com.Cloudy.Cloudy_Self_Checkout_POS.constants.ValueConstant;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.CheckoutDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.CustomerRequestBody;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.PurchaseDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.rest.CustomerRest;
import com.Cloudy.Cloudy_Self_Checkout_POS.service.CustomerService;
import com.Cloudy.Cloudy_Self_Checkout_POS.utils.CloudyUtils;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.CustomerWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class CustomerRestImpl implements CustomerRest {

    @Autowired
    CustomerService customerService;


    @Override
    public ResponseEntity<ApiResponseWrapper<CustomerWrapper>> getCustomer(UserDetails userDetails) {
        try{
            return customerService.getCustomer(userDetails);
        }catch (Exception e){
            e.printStackTrace();
        }
        return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG );
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<CustomerWrapper>> updateCustomer(UserDetails userDetails, CustomerRequestBody request) {
        try{
            return  customerService.updateCustomer(userDetails, request);
        }catch (Exception e){
            e.printStackTrace();
        }
        return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG );
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<String>> uploadProfileImage(UserDetails userDetails, MultipartFile file) {
        try{
            return  customerService.uploadProfileImg(userDetails, file);
        }catch (Exception e){
            e.printStackTrace();
        }
        return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG );
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<Object>> generateVoucherPdf(UserDetails userDetails, CheckoutDTO checkoutData) {
        try{
            return  customerService.generateVoucherPdf(userDetails.getUsername(),checkoutData);
        }catch (Exception e){
            e.printStackTrace();
        }
        return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG );
    }

    @Override
    public ResponseEntity<Resource> servePdf(UserDetails userDetails, String filename) {
        try{
            return  customerService.servePdf(userDetails.getUsername(),filename);
        }catch (Exception e){
            e.printStackTrace();
        }
        return ResponseEntity.internalServerError().build();
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<Object>> getAllPdfs(UserDetails userDetails) {
        try{
            return  customerService.getAllPdf(userDetails.getUsername());
        }catch (Exception e){
            e.printStackTrace();
        }
        return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG );
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<List<PurchaseDTO>>> getPurchaseHistory(UserDetails userDetails) {
        try{
            return  customerService.getPurchaseHistory(userDetails.getUsername());
        }catch (Exception e){
            e.printStackTrace();
        }
        return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG );
    }

}
