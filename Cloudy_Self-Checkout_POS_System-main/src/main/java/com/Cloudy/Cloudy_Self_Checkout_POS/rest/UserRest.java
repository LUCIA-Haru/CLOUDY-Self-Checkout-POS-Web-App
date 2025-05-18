package com.Cloudy.Cloudy_Self_Checkout_POS.rest;

import com.Cloudy.Cloudy_Self_Checkout_POS.dto.RegisterationRequestBody;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ResponseBody.UserResponseBody;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.*;

@RequestMapping(path = "/user")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")

public interface UserRest {

    @PostMapping(path = "/signup")
    public ResponseEntity<ApiResponseWrapper<UserResponseBody>> registerCustomer(@RequestBody @Valid RegisterationRequestBody requestBody);



    @PostMapping(path = "/verify-otp")
    public ResponseEntity<ApiResponseWrapper<UserResponseBody>> verifyOtp(
            @RequestBody @Valid RegisterationRequestBody requestBody,
            @RequestParam String otp
    );
    @PostMapping(path = "/verify-otp-pass")
    public ResponseEntity<ApiResponseWrapper<String>> verifyOtpPass(
            @RequestParam String email,
            @RequestParam String otp
    );

    @PostMapping(path = "/login")
    public  ResponseEntity<ApiResponseWrapper<UserResponseBody>> login(@RequestBody RegisterationRequestBody requestBody);

    @PutMapping("/changePassword/{id}")
    public ResponseEntity<ApiResponseWrapper<String>> changePassword(@PathVariable Long id,
                                                                     @RequestParam String oldPassword,@RequestParam String newPassword);

    @PutMapping("/forgotPassword")
    public ResponseEntity<ApiResponseWrapper<String>> forgotPassword(@RequestParam String email);

    @PutMapping("/resetPassword")
    public ResponseEntity<ApiResponseWrapper<String>> resetPassword(@RequestParam String email,@RequestParam String password);
}
