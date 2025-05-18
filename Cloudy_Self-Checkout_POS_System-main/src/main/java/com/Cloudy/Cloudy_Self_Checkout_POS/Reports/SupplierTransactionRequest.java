package com.Cloudy.Cloudy_Self_Checkout_POS.Reports;

import com.Cloudy.Cloudy_Self_Checkout_POS.dto.ProductDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SupplierTransactionRequest {
    private Long supplierId;
    private Long brandId;
    private Long categoryId;
    private int quantity;
    private LocalDateTime transactionDate;
    private LocalDate manuDate;
    private LocalDate expDate;
    private Long productId;
}
