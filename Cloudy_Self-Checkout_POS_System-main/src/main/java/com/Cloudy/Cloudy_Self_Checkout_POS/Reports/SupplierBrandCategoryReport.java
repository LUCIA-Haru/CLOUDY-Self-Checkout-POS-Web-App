package com.Cloudy.Cloudy_Self_Checkout_POS.Reports;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierBrandCategoryReport {
    private Long supplierId;
    private String supplierName;
    private Long brandId;
    private String brandName;
    private Long categoryId;
    private String categoryName;
    private Double importPercentage;
    private Boolean isActive;

}
