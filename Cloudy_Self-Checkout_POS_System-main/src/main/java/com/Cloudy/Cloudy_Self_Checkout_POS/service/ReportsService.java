package com.Cloudy.Cloudy_Self_Checkout_POS.service;

import com.Cloudy.Cloudy_Self_Checkout_POS.Reports.*;
import com.Cloudy.Cloudy_Self_Checkout_POS.customException.CustomSystemException;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.SupplierTransactionDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.loyaltyPointsHistoryDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ListWrapper;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

public interface ReportsService {
    ResponseEntity<ApiResponseWrapper<SupplierTransactionDTO>> purchaseTransactions(String username, SupplierTransactionRequest request);

    ResponseEntity<ApiResponseWrapper<ListWrapper<SupplierBrandCategoryReport>>> generateSupplierReports(String username, int page, int size) throws CustomSystemException;

    ResponseEntity<ApiResponseWrapper<ListWrapper<ProductSupplierReport>>> generateProductSuppliersReport(String username, int page, int size) throws CustomSystemException;

    ResponseEntity<ApiResponseWrapper<List<loyaltyPointsHistoryDTO>>> getLoyaltyPointsTransactionsHistory(String username);
    // Sales and Revenue Analytics
    ResponseEntity<ApiResponseWrapper<ListWrapper<RevenueOverTimeReport>>> getRevenueOverTime(String username, String period, LocalDate startDate, LocalDate endDate) throws CustomSystemException;
    ResponseEntity<ApiResponseWrapper<ListWrapper<RevenueByCategoryReport>>> getRevenueByCategory(String username, LocalDate startDate, LocalDate endDate) throws CustomSystemException;
    ResponseEntity<ApiResponseWrapper<ListWrapper<CouponDiscountReport>>> getCouponDiscountImpact(String username, LocalDate startDate, LocalDate endDate) throws CustomSystemException;
    // Customer Analytics
    ResponseEntity<ApiResponseWrapper<ListWrapper<CustomerPurchaseReport>>> getCustomerPurchaseTrends(String username, LocalDate startDate, LocalDate endDate) throws CustomSystemException;
    ResponseEntity<ApiResponseWrapper<ListWrapper<LoyaltyPointsReport>>> getLoyaltyPointsUsage(String username, LocalDate startDate, LocalDate endDate) throws CustomSystemException;
    ResponseEntity<ApiResponseWrapper<ListWrapper<CustomerDemographicsReport>>> getCustomerDemographics(String username) throws CustomSystemException;
    // Product and Inventory Analytics
    ResponseEntity<ApiResponseWrapper<ListWrapper<TopSellingProductReport>>> getTopSellingProducts(String username, LocalDate startDate, LocalDate endDate) throws CustomSystemException;
    ResponseEntity<ApiResponseWrapper<ListWrapper<StockLevelReport>>> getStockLevelsByCategory(String username) throws CustomSystemException;
    ResponseEntity<ApiResponseWrapper<ListWrapper<ProductExpiryReport>>> getProductExpiryAlerts(String username, int daysThreshold) throws CustomSystemException;
    // Supplier and Brand Analytics
    ResponseEntity<ApiResponseWrapper<ListWrapper<SupplierPerformanceReport>>> getSupplierPerformance(String username, LocalDate startDate, LocalDate endDate) throws CustomSystemException;
    ResponseEntity<ApiResponseWrapper<ListWrapper<BrandPopularityReport>>> getBrandPopularity(String username, LocalDate startDate, LocalDate endDate) throws CustomSystemException;
    // Staff and Operational Analytics
    ResponseEntity<ApiResponseWrapper<ListWrapper<StaffActivityReport>>> getStaffActivity(String username, LocalDate startDate, LocalDate endDate) throws CustomSystemException;
    ResponseEntity<ApiResponseWrapper<ListWrapper<OtpFailureReport>>> getOtpFailureAlerts(String username) throws CustomSystemException;
}
