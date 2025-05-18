package com.Cloudy.Cloudy_Self_Checkout_POS.POJO;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

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
import java.util.Set;

@Data
@Builder
@Entity
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "coupon")
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long couponId;

    @Column(name = "guid", nullable = false, updatable = false)
    private String guid;

    @Column(nullable = false, unique = true)
    private String couponCode;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal discountAmount;

    @Column(nullable = false)
    private BigDecimal minPurchaseAmount;

    @Column
    private LocalDate expirationDate;

    @Column(nullable = false)
    private boolean isActive;

    @Column
    private LocalDateTime createdOn;

    @Column
    private String createdBy;

    @Column
    private LocalDateTime updatedOn;

    @Column
    private String updatedBy;


    // Many-to-Many relationship with Customer
    @ManyToMany(mappedBy = "coupons", fetch = FetchType.LAZY)
    private Set<Customer> customers;
}
