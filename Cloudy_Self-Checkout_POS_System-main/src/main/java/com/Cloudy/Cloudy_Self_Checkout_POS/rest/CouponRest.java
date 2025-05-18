package com.Cloudy.Cloudy_Self_Checkout_POS.rest;


import com.Cloudy.Cloudy_Self_Checkout_POS.customException.CustomSystemException;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.CouponAssignDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.CouponDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RequestMapping(path = "/coupon")
public interface CouponRest {
    @PostMapping
    @PreAuthorize("hasAnyAuthority( 'STAFF', 'MANAGER', 'ADMIN')")
    @Operation(summary = "Create a new coupon", description = "Generates or adds a coupon based on the provided details")
    @ApiResponse(responseCode = "201", description = "Coupon created successfully")
    @ApiResponse(responseCode = "403", description = "Unauthorized access")
    public ResponseEntity<ApiResponseWrapper<CouponDTO>> generateCoupon(@AuthenticationPrincipal UserDetails userDetails, @RequestBody CouponDTO request) throws CustomSystemException;

    @PostMapping("/apply")
    @PreAuthorize("hasAnyAuthority('CUSTOMER')")
    public ResponseEntity<ApiResponseWrapper<BigDecimal>> useCoupon(@AuthenticationPrincipal UserDetails userDetails,@RequestBody Map<String, String> request) throws CustomSystemException;


    @PostMapping("/assign")
    @PreAuthorize("hasAnyAuthority('MANAGER','ADMIN')")
    public ResponseEntity<ApiResponseWrapper<String>> assignCoupon(@AuthenticationPrincipal UserDetails userDetails, @RequestParam Long cusId, @RequestParam String CouponCode ) throws CustomSystemException;


    @GetMapping("/cus")
    @PreAuthorize("hasAnyAuthority('CUSTOMER')")
    public ResponseEntity<ApiResponseWrapper<List<CouponDTO>>> fetchCoupons(@AuthenticationPrincipal UserDetails userDetails) throws CustomSystemException;

    @GetMapping("/admin")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ApiResponseWrapper<Map<String,Object>>> getAllCouponsAssignments(@AuthenticationPrincipal UserDetails userDetails) throws CustomSystemException;

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping("/{couponId}")
    public ResponseEntity<ApiResponseWrapper<CouponDTO>> updateCoupon(@AuthenticationPrincipal UserDetails userDetails, @RequestBody CouponDTO request,
                                                                      @PathVariable Long couponId) throws CustomSystemException;

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @DeleteMapping("/{couponId}")
    public ResponseEntity<ApiResponseWrapper<String>> deleteCoupon(@AuthenticationPrincipal UserDetails userDetails,@PathVariable Long couponId) throws CustomSystemException;

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @Operation(summary = "Get all coupons", description = "Retrieves a list of all coupons in the system")
    @ApiResponse(responseCode = "200", description = "Coupons retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Unauthorized access")
    ResponseEntity<ApiResponseWrapper<List<CouponDTO>>> getAllCoupons(@AuthenticationPrincipal UserDetails userDetails) throws CustomSystemException;
}
