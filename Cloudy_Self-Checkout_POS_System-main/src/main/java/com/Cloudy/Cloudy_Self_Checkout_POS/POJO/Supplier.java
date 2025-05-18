package com.Cloudy.Cloudy_Self_Checkout_POS.POJO;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;

@Data
@Builder
@Entity
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "supplier")
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "guid", nullable = false, unique = true, updatable = false)
    private String guid;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String contactEmail;

    @Column(nullable = false)
    private String contactPhone;

    @Column(nullable = false)
    private Boolean isMainSupplier; // Indicates if this supplier is the main supplier for a brand

    @Column(nullable = false)
    private Double contractDurationInMonths;

    @Column(nullable = false)
    private Boolean isActive = true; // Indicates if the supplier's contract is active

    @Column(name = "createdOn")
    private LocalDate createdOn;

    @Column(name ="createdBy")
    private String createdBy;

    @Column(name = "updatedOn")
    private LocalDate updatedOn;

    @Column(name = "updatedBy")
    private String updatedBy;

}
