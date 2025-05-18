package com.Cloudy.Cloudy_Self_Checkout_POS.Reports;
import lombok.*;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierPerformanceReport {
    private String supplierName;
    private Double totalQuantity;
    private Long transactionCount;
}
