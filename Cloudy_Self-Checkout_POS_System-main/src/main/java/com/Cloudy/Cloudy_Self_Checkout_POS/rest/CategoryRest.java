package com.Cloudy.Cloudy_Self_Checkout_POS.rest;

import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.Category;
import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.User;
import com.Cloudy.Cloudy_Self_Checkout_POS.customException.CustomSystemException;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.CategoryRequestBody;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.PaginatedResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RequestMapping(path = "/category")
public interface CategoryRest {


@PreAuthorize("hasAnyAuthority( 'STAFF', 'MANAGER', 'ADMIN')")
@PostMapping("/add")
@ResponseBody
public ResponseEntity<ApiResponseWrapper<CategoryRequestBody>> addCategory(@AuthenticationPrincipal UserDetails userDetails,
                                                                         @RequestBody(required = true) CategoryRequestBody categoryRequestBody);



@GetMapping("/all")
@ResponseBody
public ResponseEntity<ApiResponseWrapper<PaginatedResponse<Category>>> getAllCategory(
        @RequestParam(required = false) String filterValue,
        @RequestParam(name = "page") int page,
        @RequestParam(name = "size") int size
        )throws CustomSystemException;


@GetMapping("/get/{id}")
@ResponseBody
public ResponseEntity<ApiResponseWrapper<Category>> getCategoryById(@PathVariable("id") Long id) throws CustomSystemException;

@PreAuthorize("hasAnyAuthority( 'STAFF', 'MANAGER', 'ADMIN')")
@PostMapping("/update/{id}")
public ResponseEntity<ApiResponseWrapper<Category>> updateCategoryByID (@AuthenticationPrincipal UserDetails userDetails,
                                                                        @PathVariable("id") Long id,
                                                                        @RequestBody(required = true) CategoryRequestBody categoryRequestBody);
@PreAuthorize("hasAnyAuthority( 'STAFF', 'MANAGER', 'ADMIN')")
@DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<String>> deleteCategory(@AuthenticationPrincipal UserDetails userDetails,@PathVariable("id") Long id);
}