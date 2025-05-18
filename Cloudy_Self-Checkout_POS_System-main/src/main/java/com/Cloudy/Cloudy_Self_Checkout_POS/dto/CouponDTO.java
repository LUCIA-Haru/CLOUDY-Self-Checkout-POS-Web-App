package com.Cloudy.Cloudy_Self_Checkout_POS.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponDTO {
    private Long couponId;
    private String guid;
    private String couponCode;
    private BigDecimal discountAmount;
    private BigDecimal minPurchaseAmount;
    private LocalDate expirationDate;
    private boolean isActive;
    private LocalDateTime CreatedOn;
    private String CreatedBy;
    private LocalDateTime UpdatedOn;
    private String UpdatedBy;
    private String assignedTo;

    public CouponDTO(Long couponId, String couponCode, BigDecimal discountAmount, BigDecimal minPurchaseAmount,
                     LocalDate expirationDate, boolean isActive, String assignedTo) {
        this.couponId = couponId;
        this.couponCode = couponCode;
        this.discountAmount = discountAmount;
        this.minPurchaseAmount = minPurchaseAmount;
        this.expirationDate = expirationDate;
        this.isActive = isActive;
        this.assignedTo = assignedTo;
    }
}
