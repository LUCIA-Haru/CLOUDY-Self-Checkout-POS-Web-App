package com.Cloudy.Cloudy_Self_Checkout_POS.POJO;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.util.Date;

@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DynamicUpdate
@DynamicInsert
@DiscriminatorValue("Staff")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Table(name = "staff", indexes = {
        @Index(name = "idx_staff_guid", columnList = "guid")
})
public class Staff extends User{

    @Column(name = "guid", nullable = false, unique = true, updatable = false)
    private String guid;

    @Column(name = "firstName")
    private String firstName;

    @Column(name = "lastName")
    private String lastName;

    @Column(name = "address")
    private String address;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "dob")
    private LocalDate dob;

    @Column(name="phoneNo",unique = true)
    private String phoneNo;

    @Column(name="department")
    private String department;

    @Column(name = "position")
    private String position;

    @Column(name = "createdBy")
    private String createdBy;

}
