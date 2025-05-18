package com.Cloudy.Cloudy_Self_Checkout_POS.rest;


import com.Cloudy.Cloudy_Self_Checkout_POS.dto.CheckoutDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.CustomerRequestBody;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.PurchaseDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.CustomerWrapper;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping(path = "/customer")
public interface CustomerRest {

    @PreAuthorize("hasAnyAuthority('CUSTOMER')")
    @GetMapping(path = "/get")
    public ResponseEntity<ApiResponseWrapper<CustomerWrapper>> getCustomer(@AuthenticationPrincipal UserDetails userDetails);

    @PreAuthorize("hasAnyAuthority('CUSTOMER')")
    @PostMapping(path = "/update")
    public ResponseEntity<ApiResponseWrapper<CustomerWrapper>> updateCustomer(@AuthenticationPrincipal UserDetails userDetails, @RequestBody CustomerRequestBody request);

    @PreAuthorize("hasAnyAuthority('CUSTOMER')")
    @PostMapping(path = "/uploadImg")
    public ResponseEntity<ApiResponseWrapper<String>> uploadProfileImage(@AuthenticationPrincipal UserDetails userDetails,
                                                                         @RequestParam("file") MultipartFile file);

    @PreAuthorize("hasAnyAuthority('CUSTOMER')")
    @PostMapping(path = "/voucher")
    public ResponseEntity<ApiResponseWrapper<Object>> generateVoucherPdf(@AuthenticationPrincipal UserDetails userDetails,
                                                                         @RequestBody CheckoutDTO checkoutData);

    @PreAuthorize("hasAnyAuthority('CUSTOMER')")
    @GetMapping(path = "/voucher/{filename}")
    public ResponseEntity<Resource> servePdf(@AuthenticationPrincipal UserDetails userDetails,
                                             @PathVariable String filename);

    @PreAuthorize("hasAnyAuthority('CUSTOMER')")
    @GetMapping(path = "/vouchers")
    public ResponseEntity<ApiResponseWrapper<Object>> getAllPdfs(@AuthenticationPrincipal UserDetails userDetails
                                                               );

    @PreAuthorize("hasAnyAuthority('CUSTOMER')")
    @GetMapping(path = "/history")
    public ResponseEntity<ApiResponseWrapper<List<PurchaseDTO>>> getPurchaseHistory(@AuthenticationPrincipal UserDetails userDetails
    );
}