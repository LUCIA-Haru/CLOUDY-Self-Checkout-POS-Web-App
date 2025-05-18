package com.Cloudy.Cloudy_Self_Checkout_POS.POJO;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.util.Date;
import java.util.Set;


//-----------------------------------------------
@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DynamicUpdate
@DynamicInsert
@DiscriminatorValue("Customer")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Table(name = "customer", indexes = {
        @Index(name = "idx_cus_guid", columnList = "guid")
})
public class Customer extends User {

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

    @Column(name="phoneNo" ,unique = true)
    private String phoneNo;

    // Many-to-Many relationship with Coupon
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "customer_coupon",
            joinColumns = @JoinColumn(name = "customer_id"),
            inverseJoinColumns = @JoinColumn(name = "coupon_id")
    )
    private Set<Coupon> coupons;

}
