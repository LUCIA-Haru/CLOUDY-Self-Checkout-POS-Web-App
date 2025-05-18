package com.Cloudy.Cloudy_Self_Checkout_POS.Reports;

import lombok.*;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockLevelReport {
    private Long categoryId;
    private String categoryName;
    private Integer totalProducts;
    private Integer totalStockUnits;
    private Integer lowStockProducts;
    private List<LowStockProduct> lowStockProductsDetails;

    @Getter
    @Builder
    public static class LowStockProduct {
        private Long productId;
        private String productName;
    }
}
