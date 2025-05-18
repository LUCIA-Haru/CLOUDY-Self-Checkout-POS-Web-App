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
public class AdminDashboardDTO {
    private List<StaffActivity> staffActivity;
    private List<CustomerTrend> customerPurchaseTrends;
    private List<LoyaltyPointsUsage> loyaltyPointsUsage;
    private List<CouponDiscount> couponDiscountImpact;
    private long totalCustomerCount;
    private long totalStaffCount;
    private long totalProductCount;
    private long totalCategoryCount;
    private long totalBrandCount;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class StaffActivity {
        private String userId;
        private String firstName;
        private String lastName;
        private long transactionCount;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class CustomerTrend {
        private long userId;
        private String firstName;
        private String lastName;
        private long purchaseCount;
        private double totalSpent;
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
