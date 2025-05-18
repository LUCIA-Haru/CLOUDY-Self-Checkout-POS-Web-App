package com.Cloudy.Cloudy_Self_Checkout_POS.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
        private Long productId;
        private String productGuid;
        private String productName;
        private Long categoryId;
        private String category;
        private Long transactionId;
        private String productDesc;
        private Long brandId;
        private String brand;
        private BigDecimal price;
        private String currency;
        private Integer stockUnit;
        private LocalDate expDate;
        private LocalDate manuDate;
        private Integer rating;
        private List<ImageDTO> imgUrls;
        private LocalDateTime createdOn;
        private String createdBy;
        private LocalDateTime updatedOn;
        private String updatedBy;
        private String barcode;
        private Boolean hasDiscount;
        // Separate Discount Object
        private DiscountDTO discount;

}
