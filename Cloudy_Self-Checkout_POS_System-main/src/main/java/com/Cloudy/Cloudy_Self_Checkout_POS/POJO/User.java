package com.Cloudy.Cloudy_Self_Checkout_POS.POJO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.Date;

@SuperBuilder // Add this annotation
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@DynamicUpdate
@DynamicInsert
@Inheritance(strategy = InheritanceType.JOINED) // JOINED inheritance strategy
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING) //add @DiscriminatorValue("CUSTOMER") like this in child table
@Table(name = "cloudy_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @JsonIgnore
    @Column(name = "password", nullable = false, length = 1000)
    private String password;

    @Column(name = "email", nullable = false, unique = true, length = 320)
    private String email;

    @Column(name = "status",nullable = false)
    private Boolean status;

    @Column(name = "role")
    private String role;

    @Column(name = "createdOn", nullable = false)
    private LocalDateTime createdOn;

    @Column(name = "updatedOn")
    private LocalDateTime updatedOn;

    @Column(name = "profile_photo",columnDefinition = "TEXT")
    private String profilePhoto;


}
