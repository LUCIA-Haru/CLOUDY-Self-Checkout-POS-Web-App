package com.Cloudy.Cloudy_Self_Checkout_POS.dto;

import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.Brand;
import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.Category;
import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierTransactionDTO {
    private Long transactionId;
    private String guid;
    private Long supplierId;
    private Long  brandId;
    private Long  categoryId;
    private int quantity;
    private LocalDateTime transactionDate;
    private String createdBy;

    private List<ProductDTO> product;
}
