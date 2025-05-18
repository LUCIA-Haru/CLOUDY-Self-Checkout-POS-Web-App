package com.Cloudy.Cloudy_Self_Checkout_POS.dto;

import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.Purchase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class StaffDashboardDTO {
    private Activity activity;
    private List<Purchase> recentPurchases;
    private List<ExpiryAlert> expiryAlerts;
    private List<StockLevel> stockLevelsByCategory;
    private long totalProductCount;
    private long totalCategoryCount;
    private long totalBrandCount;
    private long totalCustomerCount;
    private long totalStaffCount;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Activity {
        private String firstName;
        private String lastName;
        private int transactionCount;


    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Purchase {
        private long purchaseId;
        private LocalDate date;
        private double totalAmount;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class ExpiryAlert {
        private long productId;
        private String productName;
        private String barcode;
        private LocalDate expiryDate;
        private int unitsInStock;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class StockLevel {
        private Long categoryId;
        private String categoryName;
        private Integer totalProducts;
        private Integer totalStockUnits;
        private Integer lowStockProducts;
        private List<LowStockProduct> lowStockProductsDetails;


    }
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class LowStockProduct {
        private Long productId;
        private String productName;
    }
}
