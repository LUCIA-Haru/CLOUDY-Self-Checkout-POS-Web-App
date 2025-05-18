package com.Cloudy.Cloudy_Self_Checkout_POS.rest;

import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.LoyaltyPoints;
import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.SupplierTransaction;
import com.Cloudy.Cloudy_Self_Checkout_POS.Reports.*;
import com.Cloudy.Cloudy_Self_Checkout_POS.customException.CustomSystemException;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.SupplierTransactionDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.loyaltyPointsHistoryDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ListWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RequestMapping(path = "/v1")
public interface ReportsRest {
    @PreAuthorize("hasAnyAuthority('MANAGER')")
    @PostMapping("/supplier-transactions")
    public ResponseEntity<ApiResponseWrapper<SupplierTransactionDTO>> purchaseTransaction(@AuthenticationPrincipal UserDetails userDetails,
                                                                                          @RequestBody SupplierTransactionRequest request);

    @PreAuthorize("hasAnyAuthority('MANAGER')")
    @GetMapping("/suppliers/report")
    public  ResponseEntity<ApiResponseWrapper<ListWrapper<SupplierBrandCategoryReport>>> generateSupplierReports(@AuthenticationPrincipal UserDetails userDetails,
                                                                                                                 @RequestParam(name = "page") int page,
                                                                                                                 @RequestParam(name = "size",defaultValue = "10") int size )
            throws CustomSystemException;

    @PreAuthorize("hasAnyAuthority('MANAGER')")
    @GetMapping("/product-supplier/report")
    public  ResponseEntity<ApiResponseWrapper<ListWrapper<ProductSupplierReport>>> generateProductSuppliersReport(@AuthenticationPrincipal UserDetails userDetails,
                                                                                                                  @RequestParam(name = "page") int page,
                                                                                                                  @RequestParam(name = "size",defaultValue = "10") int size )
            throws CustomSystemException;

    @PreAuthorize("hasAnyAuthority('CUSTOMER')")
    @GetMapping("/transactions/loyaltypoints")
    public ResponseEntity<ApiResponseWrapper<List<loyaltyPointsHistoryDTO>>> getLoyaltyPointsTransactionsHistory(@AuthenticationPrincipal UserDetails userDetails) throws CustomSystemException;


    // Sales and Revenue Analytics
    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    @GetMapping("/revenue-over-time")
    ResponseEntity<ApiResponseWrapper<ListWrapper<RevenueOverTimeReport>>> getRevenueOverTime(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String period,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) throws CustomSystemException;

    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    @GetMapping("/revenue-by-category")
    ResponseEntity<ApiResponseWrapper<ListWrapper<RevenueByCategoryReport>>> getRevenueByCategory(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) throws CustomSystemException;

    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    @GetMapping("/coupon-discount-impact")
    ResponseEntity<ApiResponseWrapper<ListWrapper<CouponDiscountReport>>> getCouponDiscountImpact(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) throws CustomSystemException;

    // Customer Analytics
    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    @GetMapping("/customer-purchase-trends")
    ResponseEntity<ApiResponseWrapper<ListWrapper<CustomerPurchaseReport>>> getCustomerPurchaseTrends(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) throws CustomSystemException;

    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    @GetMapping("/loyalty-points-usage")
    ResponseEntity<ApiResponseWrapper<ListWrapper<LoyaltyPointsReport>>> getLoyaltyPointsUsage(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) throws CustomSystemException;

    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    @GetMapping("/customer-demographics")
    ResponseEntity<ApiResponseWrapper<ListWrapper<CustomerDemographicsReport>>> getCustomerDemographics(
            @AuthenticationPrincipal UserDetails userDetails) throws CustomSystemException;

    // Product and Inventory Analytics
    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN','STAFF')")
    @GetMapping("/top-selling-products")
    ResponseEntity<ApiResponseWrapper<ListWrapper<TopSellingProductReport>>> getTopSellingProducts(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) throws CustomSystemException;

    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN','STAFF')")
    @GetMapping("/stock-levels-by-category")
    ResponseEntity<ApiResponseWrapper<ListWrapper<StockLevelReport>>> getStockLevelsByCategory(
            @AuthenticationPrincipal UserDetails userDetails) throws CustomSystemException;

    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN','STAFF')")
    @GetMapping("/product-expiry-alerts")
    ResponseEntity<ApiResponseWrapper<ListWrapper<ProductExpiryReport>>> getProductExpiryAlerts(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam int daysThreshold) throws CustomSystemException;

    // Supplier and Brand Analytics
    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    @GetMapping("/supplier-performance")
    ResponseEntity<ApiResponseWrapper<ListWrapper<SupplierPerformanceReport>>> getSupplierPerformance(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) throws CustomSystemException;

    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    @GetMapping("/brand-popularity")
    ResponseEntity<ApiResponseWrapper<ListWrapper<BrandPopularityReport>>> getBrandPopularity(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) throws CustomSystemException;

    // Staff and Operational Analytics
    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    @GetMapping("/staff-activity")
    ResponseEntity<ApiResponseWrapper<ListWrapper<StaffActivityReport>>> getStaffActivity(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) throws CustomSystemException;

    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    @GetMapping("/otp-failure-alerts")
    ResponseEntity<ApiResponseWrapper<ListWrapper<OtpFailureReport>>> getOtpFailureAlerts(
            @AuthenticationPrincipal UserDetails userDetails) throws CustomSystemException;

}
