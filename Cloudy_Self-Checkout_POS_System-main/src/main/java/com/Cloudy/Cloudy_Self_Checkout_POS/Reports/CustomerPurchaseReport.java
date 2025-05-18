package com.Cloudy.Cloudy_Self_Checkout_POS.Reports;

import lombok.*;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerPurchaseReport {
    private Long customerId;
    private String firstName;
    private String lastName;
    private Long purchaseCount;
    private BigDecimal totalSpent;
}
