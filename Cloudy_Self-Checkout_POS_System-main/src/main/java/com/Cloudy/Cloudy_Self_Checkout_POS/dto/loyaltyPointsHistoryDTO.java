package com.Cloudy.Cloudy_Self_Checkout_POS.dto;

import com.Cloudy.Cloudy_Self_Checkout_POS.constants.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class loyaltyPointsHistoryDTO {
    private String username;
    private Long purchaseId;
    private BigDecimal finalAmount;
    private Integer pointsEarned;
    private Integer pointsUsed;
    private TransactionType transactionType;
    private LocalDateTime transactionDate;
}
