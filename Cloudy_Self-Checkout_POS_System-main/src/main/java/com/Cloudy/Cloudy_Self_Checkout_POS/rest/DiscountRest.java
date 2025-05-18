package com.Cloudy.Cloudy_Self_Checkout_POS.rest;

import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.Discount;
import com.Cloudy.Cloudy_Self_Checkout_POS.customException.CustomSystemException;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.DiscountBarcodeDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.DiscountDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.PaginatedResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/discount")
public interface DiscountRest {

    @PostMapping
    @PreAuthorize("hasAnyAuthority( 'STAFF', 'MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponseWrapper<DiscountDTO>> addDiscount(@RequestBody(required = true) DiscountDTO discountDTO,
                                                                       @AuthenticationPrincipal UserDetails userDetails);

    @PostMapping("/{productId}")
    @PreAuthorize("hasAnyAuthority( 'STAFF', 'MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponseWrapper<DiscountDTO>> updateDiscount(@RequestBody(required = true) DiscountDTO discountDTO,
                                                                       @AuthenticationPrincipal UserDetails userDetails,
                                                                          @PathVariable Long productId);

    @DeleteMapping("/{discountId}")
    @PreAuthorize("hasAnyAuthority( 'STAFF', 'MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponseWrapper<String>> deleteDiscount(@AuthenticationPrincipal UserDetails userDetails,
                                                                          @PathVariable Long discountId);

    @PreAuthorize("hasAnyAuthority( 'STAFF', 'MANAGER', 'ADMIN','CUSTOMER')")
    @GetMapping("/active")
    public ResponseEntity<ApiResponseWrapper<List<DiscountBarcodeDTO>>> getActiveDiscounts(@RequestParam List<String> barcodes) throws CustomSystemException;

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('STAFF', 'MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponseWrapper<PaginatedResponse<DiscountDTO>>> getAllDiscounts(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "true", required = false) String filterValue,
            @RequestParam(name = "page") int page,
            @RequestParam(name = "size",defaultValue = "10") int size )
            throws CustomSystemException;
}
