package com.Cloudy.Cloudy_Self_Checkout_POS.Reports;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductSupplierReport {
    private String productGuid;
    private String productName;
    private String brandName;
    private String categoryName;
    private Integer totalQuantity;
    private List<String> suppliers; // Distinct supplier names
    private List<ProductSupplierTransaction> transactions; // Details of each transaction
}
