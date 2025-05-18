package com.Cloudy.Cloudy_Self_Checkout_POS.utils;

import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.PaginatedResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class CloudyUtils {
    public static ResponseEntity<String> getResponseEntity(String responseMessage, HttpStatus httpStatus) {
        // Fallback response if an exception occurred
        // Ensures that a response is always returned, even in case of an error
        return new ResponseEntity<String>("{\"message\":\"" + responseMessage + "\"}", httpStatus);
    }

    public static ResponseEntity<String> getResponseEntityField(String field, String responseMessage, HttpStatus httpStatus) {
        // Fallback response if an exception occurred
        // Ensures that a response is always returned, even in case of an error
        return new ResponseEntity<String>("{\"message\":\"" + field + responseMessage + "\"}", httpStatus);
    }

    public static <T> ResponseEntity<ApiResponseWrapper<T>> getResponseEntityCustom( HttpStatus status,String message,T data) {
        String statusMessage = status.is2xxSuccessful() ? "success" : "error";
        ApiResponseWrapper<T> apiResponse = new ApiResponseWrapper<>(statusMessage, message, data);
        return ResponseEntity.status(status).body(apiResponse);
    }
    public static <T> ResponseEntity<ApiResponseWrapper<T>> getResponseEntityCustom(HttpStatus status,String message ) {
       return getResponseEntityCustom(status,message,null);
    }


    public static <T> ResponseEntity<ApiResponseWrapper<PaginatedResponse<T>>> getPaginatedResponseEntityCustom(
            HttpStatus status, String message, PaginatedResponse<T> paginatedResponse) {

        String statusMessage = status.is2xxSuccessful() ? "success" : "error";

        // Wrap the PaginatedResponse object inside ApiResponseWrapper
        ApiResponseWrapper<PaginatedResponse<T>> apiResponse = new ApiResponseWrapper<>(statusMessage, message, paginatedResponse);

        return ResponseEntity.status(status).body(apiResponse);
    }



}
