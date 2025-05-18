package com.Cloudy.Cloudy_Self_Checkout_POS.restImpl;

import com.Cloudy.Cloudy_Self_Checkout_POS.constants.ValueConstant;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.RegisterationRequestBody;
import com.Cloudy.Cloudy_Self_Checkout_POS.rest.UserRest;
import com.Cloudy.Cloudy_Self_Checkout_POS.service.UserService;
import com.Cloudy.Cloudy_Self_Checkout_POS.utils.CloudyUtils;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ResponseBody.UserResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserRestImpl implements UserRest {

    @Autowired
    UserService userService;

    @Override
    public ResponseEntity<ApiResponseWrapper<UserResponseBody>> registerCustomer(RegisterationRequestBody requestBody) {
        try{
          return userService.signUp(requestBody);
        }catch (Exception e){
            e.printStackTrace();
        }
        return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR,ValueConstant.SOMETHING_WENT_WRONG );
    }



    @Override
    public ResponseEntity<ApiResponseWrapper<UserResponseBody>> verifyOtp(RegisterationRequestBody requestBody, String otp) {
        try{
            return userService.verifyOtp(requestBody,otp);
        }catch (Exception e){
            e.printStackTrace();
        }
        return CloudyUtils.getResponseEntityCustom( HttpStatus.INTERNAL_SERVER_ERROR,ValueConstant.SOMETHING_WENT_WRONG);
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<String>> verifyOtpPass(String email, String otp) {
        try{
            return userService.verifyOtpPass(email,otp);
        }catch (Exception e){
            e.printStackTrace();
        }
        return CloudyUtils.getResponseEntityCustom( HttpStatus.INTERNAL_SERVER_ERROR,ValueConstant.SOMETHING_WENT_WRONG);
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<UserResponseBody>> login(RegisterationRequestBody requestBody) {
        try{
            return userService.login(requestBody);
        }catch (Exception e){
            e.printStackTrace();
        }
        return CloudyUtils.getResponseEntityCustom( HttpStatus.INTERNAL_SERVER_ERROR,ValueConstant.SOMETHING_WENT_WRONG);
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<String>> changePassword(Long id, String oldPassword, String newPassword) {
        try{
            return userService.changePassword(id,oldPassword,newPassword);
        }catch (Exception e){
            e.printStackTrace();
        }
        return CloudyUtils.getResponseEntityCustom( HttpStatus.INTERNAL_SERVER_ERROR,ValueConstant.SOMETHING_WENT_WRONG);
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<String>> forgotPassword( String email) {
        try{
            return userService.forgotPass(email);
        }catch (Exception e){
            e.printStackTrace();
        }
        return CloudyUtils.getResponseEntityCustom( HttpStatus.INTERNAL_SERVER_ERROR,ValueConstant.SOMETHING_WENT_WRONG);
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<String>> resetPassword(String email,String password) {
        try{
            return userService.resetPass(email,password);
        }catch (Exception e){
            e.printStackTrace();
        }
        return CloudyUtils.getResponseEntityCustom( HttpStatus.INTERNAL_SERVER_ERROR,ValueConstant.SOMETHING_WENT_WRONG);
    }
}
