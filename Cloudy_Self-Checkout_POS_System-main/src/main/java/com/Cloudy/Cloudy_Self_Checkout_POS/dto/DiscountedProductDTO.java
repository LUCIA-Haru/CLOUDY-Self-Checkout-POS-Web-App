package com.Cloudy.Cloudy_Self_Checkout_POS.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountedProductDTO {
    private Long productId;
    private String productName;
    private String barcode;
    private String description;
    private BigDecimal price;
    private String currency;
    private BigDecimal discountValue;
    private Boolean isPercentage;
    private LocalDate discountStartDate;
    private LocalDate discountEndDate;
    private int stockUnits;
    private List<ImageDTO> imgUrls;
}
