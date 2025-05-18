package com.Cloudy.Cloudy_Self_Checkout_POS.Reports;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductSupplierTransaction {
    private Long transactionId;
    private String supplierName;
    private Integer quantity;
    private LocalDateTime transactionDate;
}
