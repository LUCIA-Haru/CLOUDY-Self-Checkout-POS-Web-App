package com.Cloudy.Cloudy_Self_Checkout_POS.restImpl;

import com.Cloudy.Cloudy_Self_Checkout_POS.constants.ValueConstant;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.RegisterationRequestBody;
import com.Cloudy.Cloudy_Self_Checkout_POS.rest.StaffRest;
import com.Cloudy.Cloudy_Self_Checkout_POS.service.StaffService;
import com.Cloudy.Cloudy_Self_Checkout_POS.utils.CloudyUtils;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.CustomerWrapper;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.PaginatedResponse;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ResponseBody.UserResponseBody;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.StaffWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StaffRestImpl implements StaffRest {

    @Autowired
    StaffService staffService;

    @ResponseBody
    @Override
    public ResponseEntity<ApiResponseWrapper<List<CustomerWrapper>>> getAllCustomers(@AuthenticationPrincipal UserDetails userDetails) {
        try{
            return staffService.getAllCustomers( userDetails);
        }catch (Exception e){
            e.printStackTrace();
        }
        return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG );
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<PaginatedResponse<StaffWrapper>>> getAllStaff(UserDetails userDetails,
                                                                                           String filterValue, int page, int size) {
        try{
            return staffService.getAllStaff(userDetails.getUsername(),filterValue,page,size);
        }catch (Exception e){
            e.printStackTrace();
        }
        return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG );
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<StaffWrapper>> getStaffById(UserDetails userDetails) {
        try{
            return staffService.getStaffById(userDetails.getUsername());
        }catch (Exception e){
            e.printStackTrace();
        }
        return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG );
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<StaffWrapper>> updateStaff(UserDetails userDetails, Long id, StaffWrapper staffWrapper) {
        try{
            return staffService.updateStaff(userDetails.getUsername(),id,staffWrapper);
        }catch (Exception e){
            e.printStackTrace();
        }
        return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG );
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<String>> deleteStaff(UserDetails userDetails, Long id) {
        try{
            return staffService.deleteStaff(userDetails.getUsername(),id);
        }catch (Exception e){
            e.printStackTrace();
        }
        return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG );
    }
    @Override
    public ResponseEntity<ApiResponseWrapper<UserResponseBody>> registerStaff(RegisterationRequestBody requestBody, UserDetails userDetails) {
        try{
            return staffService.staffSignUp(requestBody,userDetails.getUsername());
        }catch (Exception e){
            e.printStackTrace();
        }
        return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR,ValueConstant.SOMETHING_WENT_WRONG );
    }
}
