package com.Cloudy.Cloudy_Self_Checkout_POS.Reports;

import lombok.*;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopSellingProductReport {
    private Long productId;
    private String productName;
    private Integer totalQuantity;
    private BigDecimal totalRevenue;
    private int quantitySold;
}
