package com.Cloudy.Cloudy_Self_Checkout_POS.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterationRequestBody {
    // This pattern will match strings that:

// Contain at least one letter (A-Z or a-z).

// Contain at least one special character or digit.

    // Have a minimum length of 3 characters

    @Pattern(regexp = "^[A-Z][A-Za-z!@#$%^&*(),.?\":{}|<>0-9]{2,}$")
    @Size(min = 3, max=256)
    private String username;

    private String firstName;
    private String lastName;


    @Email
    private String email;

    @NotNull
    @NotBlank
    @Size(min = 6, max=32)
//    Minimum six characters, at least one letter and one number:
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()\\-_=+{}\\[\\]|:;\"'<>,.?/~`]).{6,}$",
            message = "Password must contain at least one letter, one number, one special character, and be at least 6 characters long.")
    private String password;

//    For staff
    private String role;
    private String createdBy;
    private String department;
    private String position;
    private String address;
    private LocalDate dob;
    private String phoneNo;
}
