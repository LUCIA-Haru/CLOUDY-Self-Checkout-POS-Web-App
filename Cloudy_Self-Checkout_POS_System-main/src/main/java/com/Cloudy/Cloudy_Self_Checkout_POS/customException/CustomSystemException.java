package com.Cloudy.Cloudy_Self_Checkout_POS.customException;

import org.springframework.http.HttpStatus;

public class CustomSystemException extends Exception {
    private String errorCode;
    private String fieldName;
    private String customMessage;

    public CustomSystemException(String message) {
        super(message);
        this.customMessage = message;
    }

    public CustomSystemException( String errorCode,String message) {
        super(message);
        this.customMessage = message;
        this.errorCode = errorCode;
    }

    public CustomSystemException(String errorCode,String message,  String fieldName) {
        super(message);
        this.customMessage = message;
        this.errorCode = errorCode;
        this.fieldName = fieldName;
    }

    public CustomSystemException(String message, Throwable cause) {
        super(message, cause);
        this.customMessage = message;
    }

    public CustomSystemException(String errorCode,String message,  String fieldName, Throwable cause) {
        super(message, cause);
        this.customMessage = message;
        this.errorCode = errorCode;
        this.fieldName = fieldName;
    }

    public CustomSystemException(HttpStatus httpStatus,String message) {
        super(message);
        this.customMessage = message;
        this.errorCode = httpStatus.toString();
    }


    public String getErrorCode() {
        return errorCode;
    }

    public String getFieldName() {
        return fieldName;
    }

    @Override
    public String getMessage() {
        return customMessage;
    }
}
