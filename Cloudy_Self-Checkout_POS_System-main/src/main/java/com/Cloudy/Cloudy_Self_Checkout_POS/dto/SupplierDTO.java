package com.Cloudy.Cloudy_Self_Checkout_POS.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierDTO {
    private Long id;
    private String guid;

    @NotBlank(message = "Supplier name is required")
    private String name;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Contact email is required")
    private String contactEmail;

    @Pattern(regexp = "\\d{10}", message = "Contact phone must be a 10-digit number")
    @NotBlank(message = "Contact phone is required")
    private String contactPhone;

    @NotNull(message = "Is main supplier flag is required")
    private Boolean isMainSupplier; // Indicates if this supplier is the main supplier for a brand

    @NotNull(message = "Contract duration is required")
    @Positive(message = "Contract duration must be greater than zero")
    private Double contractDurationInMonths;

    private Boolean isActive;

    private LocalDate createdOn;

    private String createdBy;

    private LocalDate updatedOn;

    private String updatedBy;
}
