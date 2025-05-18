package com.Cloudy.Cloudy_Self_Checkout_POS.rest;

import com.Cloudy.Cloudy_Self_Checkout_POS.dto.RegisterationRequestBody;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.CustomerWrapper;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.PaginatedResponse;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ResponseBody.UserResponseBody;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.StaffWrapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping(path = "/staff")
public interface StaffRest {
// ----------------------Manage   Customer---------------------
@PreAuthorize("hasAnyAuthority('STAFF', 'MANAGER', 'ADMIN')")
    @GetMapping(path = "/getAllCustomers")
    public ResponseEntity<ApiResponseWrapper<List<CustomerWrapper>>> getAllCustomers(@AuthenticationPrincipal UserDetails userDetails);


    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    @GetMapping
    ResponseEntity<ApiResponseWrapper<PaginatedResponse<StaffWrapper>>> getAllStaff(@AuthenticationPrincipal UserDetails userDetails,@RequestParam(required = false)
    String filterValue,
                                                                                    @RequestParam(name = "page") int page,
                                                                                    @RequestParam(name = "size",defaultValue = "10") int size );

    @PreAuthorize("hasAnyAuthority('MANAGER')")
    @PostMapping(path = "/register")
    public ResponseEntity<ApiResponseWrapper<UserResponseBody>> registerStaff(@RequestBody @Valid RegisterationRequestBody requestBody, @AuthenticationPrincipal UserDetails userDetails );


    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN','STAFF')")
    @GetMapping("/get")
    ResponseEntity<ApiResponseWrapper<StaffWrapper>> getStaffById(@AuthenticationPrincipal UserDetails userDetails);

    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN', 'STAFF')")
    @PutMapping(path = "/{id}")
    ResponseEntity<ApiResponseWrapper<StaffWrapper>> updateStaff(@AuthenticationPrincipal UserDetails userDetails, @PathVariable(required = false) Long id, @RequestBody StaffWrapper staffWrapper);

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @DeleteMapping(path = "/{id}")
    ResponseEntity<ApiResponseWrapper<String>> deleteStaff(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id);

}
