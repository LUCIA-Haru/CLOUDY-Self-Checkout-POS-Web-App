package com.Cloudy.Cloudy_Self_Checkout_POS.wrapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffWrapper {

    private String profilePhoto;
    private Long user_id;
    private String guid;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDateTime CreatedOn;
    private LocalDateTime updatedOn;
    private String role;
    private Boolean status;;
    private String address;
    private LocalDate dob;
    private String phoneNo;
    private String department;
    private String position;
    private  String createdBy;


//    public StaffWrapper(String profilePhoto, Long user_id, String guid, String username, String firstName, String lastName, String email, LocalDateTime createdOn, LocalDateTime updatedOn,
//                        String role, Integer status, String address, Date dob, String phoneNo, String department, String position, String createdBy) {
//        this.profilePhoto = profilePhoto;
//        this.user_id = user_id;
//        this.guid = guid;
//        this.username = username;
//        this.firstName = firstName;
//        this.lastName = lastName;
//        this.email = email;
//        CreatedOn = createdOn;
//        this.updatedOn = updatedOn;
//        this.role = role;
//        Status = status;
//        this.address = address;
//        this.dob = dob;
//        this.phoneNo = phoneNo;
//        this.department = department;
//        this.position = position;
//        this.createdBy = createdBy;
//    }

}
