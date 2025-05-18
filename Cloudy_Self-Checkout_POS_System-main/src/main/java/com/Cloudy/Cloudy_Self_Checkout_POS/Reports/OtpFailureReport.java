package com.Cloudy.Cloudy_Self_Checkout_POS.Reports;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpFailureReport {
    private Long userId;
    private String email;
    private Integer failedAttempts;
    private Long lockedUntil;
}
