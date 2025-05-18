package com.Cloudy.Cloudy_Self_Checkout_POS.customException;

import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ResponseBody.UserResponseBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomSystemException.class)
    public <T> ResponseEntity<ApiResponseWrapper<T>> handleCustomSystemException(CustomSystemException ex) {
        String status= String.valueOf(ex.getErrorCode());
        String message = ex.getMessage();
        String fieldName = ex.getFieldName();
        String cause = ex.getCause() != null ? ex.getCause().toString() :"N/A";
//        String response = String.format("Error: %s, Code: %s, Field: %s",
//                ex.getMessage(), ex.getErrorCode(), ex.getFieldName());
        // Use a generic object map to hold error details
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("field", fieldName);
        errorDetails.put("cause", cause);

        ApiResponseWrapper<T> apiResponse = new ApiResponseWrapper<>(status,message,null);
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

}
