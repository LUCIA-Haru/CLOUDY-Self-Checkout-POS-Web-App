package com.Cloudy.Cloudy_Self_Checkout_POS.POJO;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Data
@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "otpAuth")
public class OtpAuth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)// Many OTPs can belong to one User
    @JoinColumn(name = "user_id",nullable = true)
    private User user;

    @Column(name="otpEmail", nullable = false)
    private String otpEmail;

    @Column(name="otp", nullable = false)
    private String otp;

    @Column(name="otpCreateAt", nullable = false)
    private LocalDateTime otpCreatedAt;

    @Column(name = "otpExpiresAt", nullable = false)
    private LocalDateTime otpExpiresAt;

    @Column(name="otpFailedAttempts")
    private Integer otpFailedAttempts;

    @Column(name = "otpLockedUntil")
    private Long otpLockedUntil;



}
