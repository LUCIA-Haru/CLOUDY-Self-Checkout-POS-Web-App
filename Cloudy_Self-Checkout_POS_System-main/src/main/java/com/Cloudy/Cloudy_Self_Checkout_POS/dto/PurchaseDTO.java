package com.Cloudy.Cloudy_Self_Checkout_POS.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseDTO {
    private Long purchaseId;
    private String guid;
    private Long userId;
    private String username;
    private BigDecimal taxAmount;
    private BigDecimal originalAmount;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal couponDiscount;
    private BigDecimal finalAmount;
    private String status;
    private Long couponId;
    private LocalDateTime createdOn;
    private String currency;
    private List<PurchaseItemDTO> items;
    private TransactionDTO transaction;
    private String voucherPdfUrl;
}
