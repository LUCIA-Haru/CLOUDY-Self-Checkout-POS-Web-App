package com.Cloudy.Cloudy_Self_Checkout_POS.dto;

import lombok.Data;

@Data
public class ResetPassRequestBody {
    private String otp;
    private String email;
    private String newPassword;
}
