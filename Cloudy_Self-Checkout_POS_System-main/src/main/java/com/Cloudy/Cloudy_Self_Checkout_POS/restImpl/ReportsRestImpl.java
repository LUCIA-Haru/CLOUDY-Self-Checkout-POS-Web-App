package com.Cloudy.Cloudy_Self_Checkout_POS.restImpl;

import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.LoyaltyPoints;
import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.SupplierTransaction;
import com.Cloudy.Cloudy_Self_Checkout_POS.Reports.*;
import com.Cloudy.Cloudy_Self_Checkout_POS.constants.ValueConstant;
import com.Cloudy.Cloudy_Self_Checkout_POS.customException.CustomSystemException;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.SupplierTransactionDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.loyaltyPointsHistoryDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.rest.ReportsRest;
import com.Cloudy.Cloudy_Self_Checkout_POS.service.ReportsService;
import com.Cloudy.Cloudy_Self_Checkout_POS.utils.CloudyUtils;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ListWrapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@AllArgsConstructor
public class ReportsRestImpl implements ReportsRest {
    private final ReportsService reportService;

    @Override
    public ResponseEntity<ApiResponseWrapper<SupplierTransactionDTO>> purchaseTransaction(UserDetails userDetails, SupplierTransactionRequest request) {
        try{
            return reportService.purchaseTransactions(userDetails.getUsername(),request) ;
        }catch (Exception e){
            e.printStackTrace();
        }
        return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG );
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<ListWrapper<SupplierBrandCategoryReport>>> generateSupplierReports(UserDetails userDetails, int page, int size) throws CustomSystemException {
        try {
            return reportService.generateSupplierReports(userDetails.getUsername(),page,size);
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<ListWrapper<ProductSupplierReport>>> generateProductSuppliersReport(UserDetails userDetails, int page, int size) throws CustomSystemException {
        try {
            return reportService.generateProductSuppliersReport(userDetails.getUsername(),page,size);
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<List<loyaltyPointsHistoryDTO>>> getLoyaltyPointsTransactionsHistory(UserDetails userDetails) throws CustomSystemException {
        try {
            return reportService.getLoyaltyPointsTransactionsHistory(userDetails.getUsername());
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
    }
    @Override
    public ResponseEntity<ApiResponseWrapper<ListWrapper<RevenueOverTimeReport>>> getRevenueOverTime(UserDetails userDetails, String period, LocalDate startDate, LocalDate endDate) throws CustomSystemException {
        try {
            return reportService.getRevenueOverTime(userDetails.getUsername(), period, startDate, endDate);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<ListWrapper<RevenueByCategoryReport>>> getRevenueByCategory(UserDetails userDetails, LocalDate startDate, LocalDate endDate) throws CustomSystemException {
        try {
            return reportService.getRevenueByCategory(userDetails.getUsername(), startDate, endDate);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<ListWrapper<CouponDiscountReport>>> getCouponDiscountImpact(UserDetails userDetails, LocalDate startDate, LocalDate endDate) throws CustomSystemException {
        try {
            return reportService.getCouponDiscountImpact(userDetails.getUsername(), startDate, endDate);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<ListWrapper<CustomerPurchaseReport>>> getCustomerPurchaseTrends(UserDetails userDetails, LocalDate startDate, LocalDate endDate) throws CustomSystemException {
        try {
            return reportService.getCustomerPurchaseTrends(userDetails.getUsername(), startDate, endDate);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<ListWrapper<LoyaltyPointsReport>>> getLoyaltyPointsUsage(UserDetails userDetails, LocalDate startDate, LocalDate endDate) throws CustomSystemException {
        try {
            return reportService.getLoyaltyPointsUsage(userDetails.getUsername(), startDate, endDate);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<ListWrapper<CustomerDemographicsReport>>> getCustomerDemographics(UserDetails userDetails) throws CustomSystemException {
        try {
            return reportService.getCustomerDemographics(userDetails.getUsername());
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<ListWrapper<TopSellingProductReport>>> getTopSellingProducts(UserDetails userDetails, LocalDate startDate, LocalDate endDate) throws CustomSystemException {
        try {
            return reportService.getTopSellingProducts(userDetails.getUsername(), startDate, endDate);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<ListWrapper<StockLevelReport>>> getStockLevelsByCategory(UserDetails userDetails) throws CustomSystemException {
        try {
            return reportService.getStockLevelsByCategory(userDetails.getUsername());
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<ListWrapper<ProductExpiryReport>>> getProductExpiryAlerts(UserDetails userDetails, int daysThreshold) throws CustomSystemException {
        try {
            return reportService.getProductExpiryAlerts(userDetails.getUsername(), daysThreshold);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<ListWrapper<SupplierPerformanceReport>>> getSupplierPerformance(UserDetails userDetails, LocalDate startDate, LocalDate endDate) throws CustomSystemException {
        try {
            return reportService.getSupplierPerformance(userDetails.getUsername(), startDate, endDate);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<ListWrapper<BrandPopularityReport>>> getBrandPopularity(UserDetails userDetails, LocalDate startDate, LocalDate endDate) throws CustomSystemException {
        try {
            return reportService.getBrandPopularity(userDetails.getUsername(),startDate,endDate);
        }catch (Exception e) {
            e.printStackTrace();
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<ListWrapper<StaffActivityReport>>> getStaffActivity(UserDetails userDetails, LocalDate startDate, LocalDate endDate) throws CustomSystemException {
        try {
            return reportService.getStaffActivity(userDetails.getUsername(),startDate,endDate);
        }catch (Exception e) {
            e.printStackTrace();
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<ListWrapper<OtpFailureReport>>> getOtpFailureAlerts(UserDetails userDetails) throws CustomSystemException {
        try {
            return reportService.getOtpFailureAlerts(userDetails.getUsername());
        }catch (Exception e) {
            e.printStackTrace();
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }
}
