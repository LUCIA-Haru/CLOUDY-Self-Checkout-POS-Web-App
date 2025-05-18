package com.Cloudy.Cloudy_Self_Checkout_POS.rest;

import com.Cloudy.Cloudy_Self_Checkout_POS.dto.AdminDashboardDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.ManagerDashboardDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.StaffDashboardDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@RequestMapping(path = "/dashboard")
public interface DashboardRest {

    @GetMapping("/staff")
    @PreAuthorize("hasAnyAuthority('STAFF')")
    public ResponseEntity<ApiResponseWrapper<StaffDashboardDTO>> getStaffDashboard(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false)LocalDate endDate);

    @GetMapping("/admin")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ApiResponseWrapper<AdminDashboardDTO>> getAdminDashboard(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "monthly") String period,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false)  LocalDate endDate);

    @GetMapping("/manager")
    @PreAuthorize("hasAnyAuthority('MANAGER')")
    public ResponseEntity<ApiResponseWrapper<ManagerDashboardDTO>> getManagerDashboard(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false)LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate);
}
