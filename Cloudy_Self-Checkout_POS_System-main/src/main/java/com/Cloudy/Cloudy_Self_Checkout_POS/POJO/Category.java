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
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @Column(name = "guid", nullable = false, unique = true, updatable = false)
    private String guid;

    @Column(name = "categoryName", nullable = false, unique = true)
    private String categoryName;

    @Column(name = "categoryDesc", columnDefinition = "TEXT")
    private String categoryDesc;

    @Column(name = "aisle")
    private Integer aisle;

    @Column(name = "createdOn")
    private LocalDate createdOn;

    @Column(name ="createdBy")
    private String createdBy;

    @Column(name = "updatedOn")
    private LocalDate updatedOn;

    @Column(name = "updatedBy")
    private String updatedBy;
}
