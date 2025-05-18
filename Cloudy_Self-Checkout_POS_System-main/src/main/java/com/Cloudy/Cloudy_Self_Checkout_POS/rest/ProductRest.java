package com.Cloudy.Cloudy_Self_Checkout_POS.rest;


import com.Cloudy.Cloudy_Self_Checkout_POS.Reports.*;
import com.Cloudy.Cloudy_Self_Checkout_POS.customException.CustomSystemException;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.DiscountedProductDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.ProductDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ListWrapper;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.PaginatedResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RequestMapping("/product")
public interface ProductRest {
    @PreAuthorize("hasAnyAuthority( 'STAFF', 'MANAGER', 'ADMIN')")
    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<ApiResponseWrapper<ProductDTO>> addProduct(@AuthenticationPrincipal UserDetails userDetails,
                                                                     @RequestBody(required = true) ProductDTO requestBody);

    @GetMapping("/search/{barcode}")
    @ResponseBody
    public ResponseEntity<ApiResponseWrapper<ProductDTO>> getProductByBarcode(@PathVariable String barcode);

    @GetMapping("/search/validate-stock")
    public ResponseEntity<ApiResponseWrapper<Boolean>> validateStock(@RequestParam String barcode,
                                                                     @RequestParam Integer quantity);

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<ProductDTO>> getProductById(@AuthenticationPrincipal UserDetails userDetails,@PathVariable(value = "id") Long id);

    @GetMapping("/all")
    public ResponseEntity<ApiResponseWrapper<PaginatedResponse<ProductDTO>>> getAllProducts(@RequestParam(required = false)
            String filterValue,
                                                                                            @RequestParam(name = "page") int page,
                                                                                            @RequestParam(name = "size",defaultValue = "10") int size );
    @PreAuthorize("hasAnyAuthority( 'STAFF', 'MANAGER', 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<ProductDTO>> updateProduct(@AuthenticationPrincipal UserDetails userDetails,
                                                                        @PathVariable(value = "id") Long id, @RequestBody ProductDTO dto);
    @PreAuthorize("hasAnyAuthority( 'STAFF', 'MANAGER', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<String>> deleteProduct(@AuthenticationPrincipal UserDetails userDetails,
                                                                    @PathVariable(value = "id") Long id);


    @GetMapping("/discounts")
    public  ResponseEntity<ApiResponseWrapper<ListWrapper<DiscountedProductDTO>>> getDiscountProductsReport();



}
