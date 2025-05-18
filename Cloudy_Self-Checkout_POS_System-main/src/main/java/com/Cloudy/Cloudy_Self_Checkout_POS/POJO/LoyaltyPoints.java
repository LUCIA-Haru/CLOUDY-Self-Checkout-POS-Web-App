package com.Cloudy.Cloudy_Self_Checkout_POS.POJO;

import com.Cloudy.Cloudy_Self_Checkout_POS.constants.TransactionType;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@Entity
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "loyaltyPoints")
public class LoyaltyPoints {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loyaltyPointId;

    @Column(name = "guid", nullable = false, updatable = false)
    private String guid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id")
    private Purchase purchase;

    @Column(nullable = false)
    private Integer pointsEarned; // Points from purchases

    @Column(nullable = false)
    private Integer pointsUsed; // Points redeemed


    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType; // EARN or REDEEM

    @Column(nullable = false)
    private LocalDateTime transactionDate;
}
