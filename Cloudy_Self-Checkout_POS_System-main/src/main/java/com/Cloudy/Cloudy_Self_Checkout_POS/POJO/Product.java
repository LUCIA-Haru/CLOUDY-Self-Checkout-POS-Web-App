package com.Cloudy.Cloudy_Self_Checkout_POS.POJO;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@Entity
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(name = "productGuid", nullable = false, updatable = false)
    private String productGuid;

    @Column(name = "productName", nullable = false)
    private String productName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false) // Foreign Key column
    private Category category;

    @Column(name = "productDesc", columnDefinition = "TEXT")
    private String productDesc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false) // Foreign Key column
    private Brand brand;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "currency")
    private String currency ;

    @Column(name = "stockUnit")
    private Integer stockUnit;

    @Column(name = "hasDiscount")
    private boolean hasDiscount;

    @Column(name = "isActive", nullable = false)
    private boolean isActive;

    @Column(name = "expDate", nullable = false)
    private LocalDate expDate;

    @Column(name = "manuDate", nullable = false)
    private LocalDate manuDate;

    @Column(name = "rating")
    private Integer rating;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImages> images = new ArrayList<>();

    @Column(name = "createdOn")
    private LocalDateTime createdOn;

    @Column(name = "createdBy")
    private String createdBy;

    @Column(name = "updatedOn")
    private LocalDateTime updatedOn;

    @Column(name = "updatedBy")
    private String updatedBy;

    @Column(name = "barcode", nullable = false, updatable = false, unique = true)
    private String barcode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = true) // Foreign Key column
    private SupplierTransaction supplierTransaction;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<Discount> discounts = new ArrayList<>();
}
