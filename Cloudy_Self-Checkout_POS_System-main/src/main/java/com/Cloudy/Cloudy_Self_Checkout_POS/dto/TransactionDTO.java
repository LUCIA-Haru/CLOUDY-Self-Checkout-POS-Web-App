package com.Cloudy.Cloudy_Self_Checkout_POS.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {
    private Long transactionId;
    private String guid;
    private Long purchaseId;
    private String paymentId;
    private BigDecimal amount;
    private String status;
    private String paymentMethod;
    private LocalDateTime transactionDate;
    private String payerId;
    private LocalDateTime createdOn;
    private String failureReason;
}
