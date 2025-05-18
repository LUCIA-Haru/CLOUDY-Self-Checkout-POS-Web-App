package com.Cloudy.Cloudy_Self_Checkout_POS.Reports;

import lombok.*;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffActivityReport {
    private Long staffId;
    private String firstName;
    private String lastName;
    private Long transactionCount;
    private Long productsAdded;
    private Long purchasesProcessed;
}
