package com.Cloudy.Cloudy_Self_Checkout_POS.service;

import com.Cloudy.Cloudy_Self_Checkout_POS.customException.CustomSystemException;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.RegisterationRequestBody;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.CustomerWrapper;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.PaginatedResponse;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ResponseBody.UserResponseBody;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.StaffWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface StaffService {
    ResponseEntity<ApiResponseWrapper<List<CustomerWrapper>>> getAllCustomers(@AuthenticationPrincipal UserDetails userDetails);

    ResponseEntity<ApiResponseWrapper<PaginatedResponse<StaffWrapper>>> getAllStaff(String username,String filterValue,int page,int size) throws CustomSystemException;

    ResponseEntity<ApiResponseWrapper<StaffWrapper>> getStaffById(String username);

    ResponseEntity<ApiResponseWrapper<StaffWrapper>> updateStaff(String username,Long id,StaffWrapper staffWrapper);

    ResponseEntity<ApiResponseWrapper<String>> deleteStaff(String username,Long id);

    ResponseEntity<ApiResponseWrapper<UserResponseBody>> staffSignUp(RegisterationRequestBody requestBody, String username);
}
