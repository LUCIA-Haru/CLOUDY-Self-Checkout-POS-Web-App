package com.Cloudy.Cloudy_Self_Checkout_POS.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CouponAssignDTO {
    private String couponCode;
    private BigDecimal discountAmount;
    private BigDecimal minPurchaseAmount;
    private Boolean isActive;
    private LocalDate expirationDate;
    private String assignedTo;
}
