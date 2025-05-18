package com.Cloudy.Cloudy_Self_Checkout_POS.Reports;

import lombok.*;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoyaltyPointsReport {
    private Long customerId;
    private String firstName;
    private String lastName;
    private Integer pointsEarned;
    private Integer pointsRedeemed;
}
