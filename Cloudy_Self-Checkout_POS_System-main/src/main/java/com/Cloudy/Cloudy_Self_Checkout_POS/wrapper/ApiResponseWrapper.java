package com.Cloudy.Cloudy_Self_Checkout_POS.wrapper;

import lombok.Data;
import org.springframework.data.domain.Page;

@Data
public class ApiResponseWrapper<T> {
    private String status;
    private String message;
    private T data;

    public ApiResponseWrapper(String status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public ApiResponseWrapper(String response, Object message) {
        this.status = status;
        message = message;
    }

}
