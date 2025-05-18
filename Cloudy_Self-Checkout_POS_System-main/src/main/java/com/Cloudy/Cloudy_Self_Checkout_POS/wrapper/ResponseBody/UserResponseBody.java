package com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ResponseBody;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@AllArgsConstructor
@Data
public class UserResponseBody {

    public UserResponseBody() {
    }
    private String token;
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
    private Boolean Status;
    private Integer otpFailedAttempts;
    private String address;
    private LocalDate dob;
    private String phoneNo;
    private String department;
    private String position;
    private  String createdBy;

    public UserResponseBody(Long user_id, String username, String email, LocalDateTime createdOn, Boolean status,String role,String firstName,String lastName,String guid) {
        this.user_id = user_id;
        this.username = username;
        this.email = email;
        this.firstName=firstName;
        this.lastName=lastName;
        this.guid = guid;
        CreatedOn = createdOn;
        Status = status;
        this.role = role;
    }

    public UserResponseBody(Long user_id, String guid, String username, String firstName,
                            String lastName, String email, LocalDateTime createdOn, String role, Boolean status, String department, String position,String createdBy) {
        this.user_id = user_id;
        this.guid = guid;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        CreatedOn = createdOn;
        this.role = role;
        Status = status;
        this.department = department;
        this.position = position;
        this.createdBy = createdBy;
    }


    public UserResponseBody(String token, String profilePhoto, Long user_id, String guid, String username, String firstName, String lastName, String email,
                            LocalDateTime createdOn, LocalDateTime updatedOn,
                            String role, Boolean status, String address, LocalDate dob, String phoneNo, String department, String position, String createdBy) {
        this.token = token;
        this.profilePhoto = profilePhoto;
        this.user_id = user_id;
        this.guid = guid;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        CreatedOn = createdOn;
        this.updatedOn = updatedOn;
        this.role = role;
        Status = status;
        this.address = address;
        this.dob = dob;
        this.phoneNo = phoneNo;
        this.department = department;
        this.position = position;
        this.createdBy = createdBy;
    }

    public UserResponseBody(Long user_id, String guid, String username, String firstName,
                            String lastName, String email, LocalDateTime createdOn, String role,
                            Boolean status, String department, String position, String createdBy,
                            String address, LocalDate dob, String phoneNo) {
        this.user_id = user_id;
        this.guid = guid;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.CreatedOn = createdOn;
        this.role = role;
        this.Status = status;
        this.department = department;
        this.position = position;
        this.createdBy = createdBy;
        this.address = address;
        this.dob = dob;
        this.phoneNo = phoneNo;
    }

}
