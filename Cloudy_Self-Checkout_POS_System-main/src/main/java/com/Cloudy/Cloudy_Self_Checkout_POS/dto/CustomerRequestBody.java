package com.Cloudy.Cloudy_Self_Checkout_POS.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import lombok.Data;


import java.time.LocalDate;


@Data
public class CustomerRequestBody {
    private String firstName;
    private String lastName;

    @Email
    private String email;
    private String address;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dob;
    private String phoneNo;
}
