package com.Cloudy.Cloudy_Self_Checkout_POS.Reports;

import lombok.*;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueOverTimeReport {
    private String period;
    private BigDecimal totalRevenue;
}
