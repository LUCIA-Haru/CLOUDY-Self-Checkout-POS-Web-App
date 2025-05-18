package com.Cloudy.Cloudy_Self_Checkout_POS.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BrandDTO {
    private Long id;
    private String guid;   // Unique identifier for the brand
    @NotBlank(message = "Name cannot be blank")
    private String name;   // Name of the brand
    private String createdBy;
    private LocalDate createdOn;
    private String updatedBy;
    private LocalDate updatedOn;
    private Boolean isActive = true;
}
