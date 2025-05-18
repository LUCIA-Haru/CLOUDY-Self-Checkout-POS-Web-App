package com.Cloudy.Cloudy_Self_Checkout_POS.Reports;

import lombok.*;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductExpiryReport {
    private Long productId;
    private String productName;
    private String barcode;
    private Integer stockUnit;
    private LocalDate expiryDate;
}
