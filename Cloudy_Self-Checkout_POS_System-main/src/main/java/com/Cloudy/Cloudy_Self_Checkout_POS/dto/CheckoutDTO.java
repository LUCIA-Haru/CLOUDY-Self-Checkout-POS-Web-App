package com.Cloudy.Cloudy_Self_Checkout_POS.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckoutDTO {
    // Cart Details
    private List<CartItemDTO> cartItems; // From frontend CartService
    private double totalAmount; // Sum of discounted prices from frontend
    private double originalAmount; // New field: Sum of original prices from frontend
    private String couponCode; // Coupon applied at checkout
    private Double couponDiscount; // Calculated in backend
    private Integer loyaltyPointsUsed;
    private Double loyaltyDiscount;
    private double finalAmount; // Will include tax

    // User and Order Info
    private String username;
    private Long purchaseId; // Set by backend after order creation

    // Payment Info (optional, for PayPal response)
    private String paymentId; // Set after PayPal execution
    private String payerId; // Set after PayPal approval

    // Response fields
    private Integer pointsEarned;
    private Double discountAmount; // Calculated as originalAmount - totalAmount in response
    private double taxAmount; // Calculated as 3% of (totalAmount - couponDiscount - loyaltyPointsUsed)
    private String approvalURl;
    private String TransactionStatus;
}
