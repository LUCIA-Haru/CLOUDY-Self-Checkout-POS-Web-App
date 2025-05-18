package com.Cloudy.Cloudy_Self_Checkout_POS.dto;

import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRequestBody {

    private Long categoryId;
    private String categoryName;
    private String categoryType;
    private String categoryDesc;
    private Integer aisle;
    private String createdBy;
    private LocalDate createdOn;
    private String updatedBy;
    private LocalDate updatedOn;



}
