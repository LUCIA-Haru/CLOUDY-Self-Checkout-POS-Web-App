package com.Cloudy.Cloudy_Self_Checkout_POS.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class ImageDTO {
    private String imgUrl;
    private int priority;
    private boolean isMain;
}
