package com.Cloudy.Cloudy_Self_Checkout_POS.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscountDTO {
    private Long discountId;
    private String guid;
    private BigDecimal discountValue;
    private Boolean isPercentage;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long productId;
    private Long categoryId;
    private String productName;
    private String categoryName;

}
