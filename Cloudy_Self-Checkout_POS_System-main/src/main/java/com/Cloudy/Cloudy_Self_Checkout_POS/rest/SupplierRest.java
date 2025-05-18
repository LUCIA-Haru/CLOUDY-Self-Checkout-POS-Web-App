package com.Cloudy.Cloudy_Self_Checkout_POS.rest;


import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.Supplier;
import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.SupplierTransaction;
import com.Cloudy.Cloudy_Self_Checkout_POS.customException.CustomSystemException;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.SupplierDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.SupplierTransactionDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.PaginatedResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping(path = "suppliers")
public interface SupplierRest {
    @PreAuthorize("hasAnyAuthority('MANAGER')")
    @PostMapping
    public ResponseEntity<ApiResponseWrapper<Supplier>> addSupplier(@AuthenticationPrincipal UserDetails userDetails,
                                                                    @RequestBody SupplierDTO dto);

    @PreAuthorize("hasAnyAuthority('MANAGER')")
    @PutMapping
    public ResponseEntity<ApiResponseWrapper<Supplier>> updateSupplier(@AuthenticationPrincipal UserDetails userDetails,
                                                                    @RequestBody SupplierDTO dto);

    @PreAuthorize("hasAnyAuthority('MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<String>> deleteSupplier(@AuthenticationPrincipal UserDetails userDetails,
                                                                    @PathVariable Long id);

    @PreAuthorize("hasAnyAuthority('MANAGER')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<Supplier>> getSupplierById(@AuthenticationPrincipal UserDetails userDetails,
                                                                     @PathVariable Long id);

    @PreAuthorize("hasAnyAuthority('MANAGER')")
    @GetMapping
    public ResponseEntity<ApiResponseWrapper<PaginatedResponse<Supplier>>> getAllSupplier(@AuthenticationPrincipal UserDetails userDetails
            , @RequestParam(required = false)
                                                                                    String filterValue,
                                                                                          @RequestParam(name = "page") int page,
                                                                                          @RequestParam(name = "size",defaultValue = "10") int size ) throws CustomSystemException;

    @PreAuthorize("hasAnyAuthority('MANAGER')")
    @GetMapping("/all/transactions")
    public ResponseEntity<ApiResponseWrapper<List<SupplierTransactionDTO>>> getSupplierTransaction(@AuthenticationPrincipal UserDetails userDetails) throws CustomSystemException;
}
