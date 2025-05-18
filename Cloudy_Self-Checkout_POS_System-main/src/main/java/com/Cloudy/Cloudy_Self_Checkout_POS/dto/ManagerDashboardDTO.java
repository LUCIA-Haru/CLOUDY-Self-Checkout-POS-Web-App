package com.Cloudy.Cloudy_Self_Checkout_POS.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ManagerDashboardDTO {
    private List<RevenueOverTime> revenueOverTime;
    private List<RevenueByCategory> revenueByCategory;
    private List<Demographic> customerDemographics;
    private List<SupplierPerformance> supplierPerformance;
    private List<TopProduct> topProducts;
    private List<BrandPopularity> brandPopularity;
    private List<LoyaltyPointsUsage> loyaltyPointsUsage;
    private List<CouponDiscount> couponDiscountImpact;
    private long totalProductCount;
    private long totalCategoryCount;
    private long totalBrandCount;
    private long totalCustomerCount;
    private long totalStaffCount;


    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class TopProduct {
        private long productId;
        private String productName;
        private long quantitySold;
        private double totalRevenue;
    }
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class RevenueOverTime {
        private String period;
        private double total;
    }
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class RevenueByCategory {
        private String categoryName;
        private double total;
    }
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Demographic {
        private String ageGroup;
        private long customerCount;
    }
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class SupplierPerformance {
        private String supplierName;
        private long totalQuantity;
        private long transactionCount;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class BrandPopularity {
        private String brandName;
        private long totalQuantity;
        private double totalRevenue;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class LoyaltyPointsUsage {
        private long userId;
        private String firstName;
        private long pointsEarned;
        private long pointsUsed;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class CouponDiscount {
        private String couponCode;
        private double totalDiscount;
        private long usageCount;
    }
}
