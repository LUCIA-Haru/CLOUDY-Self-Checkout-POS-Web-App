package com.Cloudy.Cloudy_Self_Checkout_POS.wrapper;

import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.Customer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerWrapper {
    private String profilePhoto;
    private Long user_id;
    private String guid;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
    private Boolean status;
    private String role;
    private String address;
    private LocalDate dob;
    private String phoneNo;

    // Constructor to map Customer entity to CustomerWrapper
    public CustomerWrapper(Customer customer) {
        this.profilePhoto = customer.getProfilePhoto();
        this.user_id = customer.getUserId();
        this.guid = customer.getGuid();
        this.username = customer.getUsername();
        this.firstName = customer.getFirstName();
        this.lastName = customer.getLastName();
        this.email = customer.getEmail();
        this.createdOn = customer.getCreatedOn();
        this.updatedOn = customer.getUpdatedOn();
        this.status = customer.getStatus();
        this.role = customer.getRole();
        this.address = customer.getAddress();
        this.dob = customer.getDob();
        this.phoneNo = customer.getPhoneNo();
    }
}
