package com.Cloudy.Cloudy_Self_Checkout_POS.restImpl;

import com.Cloudy.Cloudy_Self_Checkout_POS.constants.ValueConstant;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.AdminDashboardDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.ManagerDashboardDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.StaffDashboardDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.rest.DashboardRest;
import com.Cloudy.Cloudy_Self_Checkout_POS.service.DashboardService;
import com.Cloudy.Cloudy_Self_Checkout_POS.utils.CloudyUtils;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
public class DashbaordRestImpl implements DashboardRest {
    @Autowired
    DashboardService dashboardService;

    @Override
    public ResponseEntity<ApiResponseWrapper<StaffDashboardDTO>> getStaffDashboard(UserDetails userDetails, LocalDate startDate, LocalDate endDate) {
        try{
            return   dashboardService.getStaffDashboard(userDetails.getUsername(), startDate, endDate);
        }catch (Exception e){
            e.printStackTrace();
        }
        return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG );
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<AdminDashboardDTO>> getAdminDashboard(UserDetails userDetails, String period, LocalDate startDate, LocalDate endDate) {
        try{
            return   dashboardService.getAdminDashboard(userDetails.getUsername(), period, startDate, endDate);
        }catch (Exception e){
            e.printStackTrace();
        }
        return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG );
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<ManagerDashboardDTO>> getManagerDashboard(UserDetails userDetails, LocalDate startDate, LocalDate endDate) {
        try{
            return  dashboardService.getManagerDashboard(userDetails.getUsername(), startDate, endDate);
        }catch (Exception e){
            e.printStackTrace();
        }
        return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG );
    }
}
