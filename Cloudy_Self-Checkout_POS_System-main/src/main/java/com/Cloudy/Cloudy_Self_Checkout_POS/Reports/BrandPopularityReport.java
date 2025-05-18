package com.Cloudy.Cloudy_Self_Checkout_POS.Reports;

import lombok.*;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrandPopularityReport {
    private String brandName;
    private Integer quantitySold;
    private BigDecimal totalRevenue;
}
