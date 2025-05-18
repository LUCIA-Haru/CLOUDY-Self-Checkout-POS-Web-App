package com.Cloudy.Cloudy_Self_Checkout_POS.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemDTO {
    private String img;
    private String barcode;
    private String name;
    private double price; // Original price
    private Double discountedPrice; // From frontend CartService
    private int quantity;
}
