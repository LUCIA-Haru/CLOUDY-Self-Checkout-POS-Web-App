package com.Cloudy.Cloudy_Self_Checkout_POS.rest;


import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.Brand;
import com.Cloudy.Cloudy_Self_Checkout_POS.customException.CustomSystemException;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.BrandDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.PaginatedResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping(path = "/brand")
public interface BrandRest {


    @PreAuthorize("hasAnyAuthority( 'STAFF', 'MANAGER', 'ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponseWrapper<BrandDTO>> addBrand(@AuthenticationPrincipal UserDetails userDetails, @RequestBody BrandDTO brandDTO);

    @PreAuthorize("hasAnyAuthority( 'STAFF', 'MANAGER', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<String>> deleteBrand(@AuthenticationPrincipal UserDetails userDetails,@PathVariable Long id);

    @PreAuthorize("hasAnyAuthority( 'STAFF', 'MANAGER', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<Brand>> getBrandByID(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id);


    @GetMapping("/all")
    public ResponseEntity<ApiResponseWrapper<PaginatedResponse<Brand>>> getAllBrand(
             @RequestParam(required = false)
                                                                              String filterValue,
                                                                         @RequestParam(name = "page") int page,
                                                                         @RequestParam(name = "size",defaultValue = "10") int size ) throws CustomSystemException;
    @PreAuthorize("hasAnyAuthority( 'STAFF', 'MANAGER', 'ADMIN')")
    @PutMapping
    public  ResponseEntity<ApiResponseWrapper<Brand>> updateBrand(@AuthenticationPrincipal UserDetails userDetails,@RequestBody BrandDTO brandDTO);
}
