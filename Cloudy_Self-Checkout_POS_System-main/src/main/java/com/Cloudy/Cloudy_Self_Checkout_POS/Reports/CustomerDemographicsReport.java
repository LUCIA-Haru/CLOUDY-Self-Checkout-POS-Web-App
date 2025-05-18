package com.Cloudy.Cloudy_Self_Checkout_POS.Reports;

import lombok.*;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDemographicsReport {
    private String ageGroup;
    private Long customerCount;
}
