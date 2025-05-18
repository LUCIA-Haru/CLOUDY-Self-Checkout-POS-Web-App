package com.Cloudy.Cloudy_Self_Checkout_POS.service;

import com.Cloudy.Cloudy_Self_Checkout_POS.customException.CustomSystemException;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.RegisterationRequestBody;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ResponseBody.UserResponseBody;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<ApiResponseWrapper<UserResponseBody>> signUp(RegisterationRequestBody requestBody);



    ResponseEntity<ApiResponseWrapper<UserResponseBody>> verifyOtp(RegisterationRequestBody requestBody,String otp);

    ResponseEntity<ApiResponseWrapper<String>> verifyOtpPass(String email,String otp);

    ResponseEntity<ApiResponseWrapper<UserResponseBody>> login(RegisterationRequestBody requestBody);

    ResponseEntity<ApiResponseWrapper<String>> changePassword(Long id,String oldPassword,String newPassword) throws CustomSystemException;

    ResponseEntity<ApiResponseWrapper<String>> forgotPass(String email) throws CustomSystemException, MessagingException;

    ResponseEntity<ApiResponseWrapper<String>> resetPass(String email,String pass) throws CustomSystemException;
}
