package com.Cloudy.Cloudy_Self_Checkout_POS.dto;

import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.Discount;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscountBarcodeDTO {
    @NotNull
    @Size(min = 1, message = "Barcode cannot be empty")
    private String barcode;
    private DiscountDTO discount;


}
