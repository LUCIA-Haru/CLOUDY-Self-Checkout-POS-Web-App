package com.Cloudy.Cloudy_Self_Checkout_POS.service;

import com.Cloudy.Cloudy_Self_Checkout_POS.customException.CustomSystemException;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.AdminDashboardDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.ManagerDashboardDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.StaffDashboardDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

public interface DashboardService {
    ResponseEntity<ApiResponseWrapper<StaffDashboardDTO>> getStaffDashboard(
            String username, LocalDate startDate, LocalDate endDate) throws CustomSystemException;

    ResponseEntity<ApiResponseWrapper<AdminDashboardDTO>> getAdminDashboard(
            String username, String period, LocalDate startDate, LocalDate endDate) throws CustomSystemException;

    ResponseEntity<ApiResponseWrapper<ManagerDashboardDTO>> getManagerDashboard(
            String username, LocalDate startDate, LocalDate endDate) throws CustomSystemException;
}
