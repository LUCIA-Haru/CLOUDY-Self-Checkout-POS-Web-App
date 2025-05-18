package com.Cloudy.Cloudy_Self_Checkout_POS.serviceImpl;


import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.Category;
import com.Cloudy.Cloudy_Self_Checkout_POS.constants.ValueConstant;
import com.Cloudy.Cloudy_Self_Checkout_POS.customException.CustomSystemException;
import com.Cloudy.Cloudy_Self_Checkout_POS.dao.*;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.AdminDashboardDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.ManagerDashboardDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.StaffDashboardDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.service.DashboardService;
import com.Cloudy.Cloudy_Self_Checkout_POS.utils.CloudyUtils;
import com.Cloudy.Cloudy_Self_Checkout_POS.utils.UserUtils;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    private final PurchaseDao purchaseDAO;
    private final ProductDao productDAO;
    private final CustomerDao customerDAO;
    private  final CategoryDao categoryDAO;
    private final BrandDAO brandDAO;
    private final StaffDao staffDAO;
    private  final SupplierTransactionDAO supplierTransactionDAO;
    private final LoyaltyPointsDAO loyaltyPointsDAO;
    private final UserUtils userUtils;

    private final String DashboardServiceImpl = "DashboardServiceImpl";

    @Override
    public ResponseEntity<ApiResponseWrapper<StaffDashboardDTO>> getStaffDashboard(String username, LocalDate startDate, LocalDate endDate) throws CustomSystemException {
        log.info("{} => getStaffDashboard() => Subject: retrieving staff dashboard ||| username: {}", DashboardServiceImpl, username);
        try {
            userUtils.getUserByUserName(username);
            if (startDate == null) startDate = LocalDate.now().minusDays(30);
            if (endDate == null) endDate = LocalDate.now();

            StaffDashboardDTO dto = new StaffDashboardDTO();

            // Staff Activity
            List<Object[]> staffActivity = purchaseDAO.findStaffActivity(startDate, endDate);
            staffActivity.stream()
                    .filter(row -> row[0].toString().equals(username))
                    .findFirst()
                    .ifPresent(row -> dto.setActivity(new StaffDashboardDTO.Activity(
                            (String) row[1], // firstName
                            (String) row[2], // lastName
                            ((int) row[3]) // transactionCount
                    )));

            // Recent Purchases
            List<Object[]> recentPurchases = purchaseDAO.findPurchasesByUserId(startDate, endDate);
            dto.setRecentPurchases(recentPurchases.stream()
                    .map(row -> new StaffDashboardDTO.Purchase(
                            ((Number) row[0]).longValue(),
                            ((java.sql.Timestamp) row[1]).toLocalDateTime().toLocalDate(),
                            ((Number) row[2]).doubleValue()
                    ))
                    .collect(Collectors.toList()));

            // Product Expiry Alerts
            List<Object[]> expiryAlerts = productDAO.findProductExpiryAlerts(30);
            dto.setExpiryAlerts(expiryAlerts.stream()
                    .map(row -> new StaffDashboardDTO.ExpiryAlert(
                            ((Number) row[0]).longValue(),
                            (String) row[1],
                            (String) row[2],
                            ((java.sql.Date) row[3]).toLocalDate(),
                            ((Number) row[4]).intValue()
                    ))
                    .collect(Collectors.toList()));

            // Stock Levels by Category
            List<Object[]> stockLevels = productDAO.findStockLevelsByCategory();
            dto.setStockLevelsByCategory(stockLevels.stream()
                    .map(row -> {
                        try {
                            ObjectMapper objectMapper = new ObjectMapper();
                            String json = (String) row[5];
                            List<StaffDashboardDTO.LowStockProduct> lowStockProducts = json != null && !json.equals("[]")
                                    ? objectMapper.readValue(json, objectMapper.getTypeFactory()
                                    .constructCollectionType(List.class, StaffDashboardDTO.LowStockProduct.class))
                                    : List.of();
                            return new StaffDashboardDTO.StockLevel(
                                    ((Number) row[0]).longValue(), // categoryId
                                    (String) row[1],               // categoryName
                                    (int) row[2],                  // totalProducts
                                    (int) row[3],                  // totalStockUnits
                                    (int) row[4],                  // lowStockProducts
                                    lowStockProducts               // lowStockProductsDetails
                            );
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to parse lowStockProductsDetails JSON", e);
                        }
                    })
                    .collect(Collectors.toList()));


            // Total Counts
            dto.setTotalProductCount(productDAO.count());
            dto.setTotalCategoryCount(categoryDAO.count());
            dto.setTotalBrandCount(brandDAO.count());
            dto.setTotalCustomerCount(customerDAO.count());
            dto.setTotalStaffCount(staffDAO.countActiveStaff());

            log.info("✅ {} => getStaffDashboard() => Subject: retrieved staff dashboard || username: {}", DashboardServiceImpl, username);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, ValueConstant.RETRIEVE, dto);
        } catch (Exception e) {
            log.error("{} => getStaffDashboard() => Unexpected Error: {}", DashboardServiceImpl, e.getMessage());
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<AdminDashboardDTO>> getAdminDashboard(String username, String period, LocalDate startDate, LocalDate endDate) throws CustomSystemException {
        log.info("{} => getAdminDashboard() => Subject: retrieving admin dashboard ||| username: {}", DashboardServiceImpl, username);
        try {
            userUtils.getUserByUserName(username);
            if (!List.of("daily", "weekly", "monthly").contains(period)) {
                log.error("{} => getAdminDashboard() => Invalid period: {}", DashboardServiceImpl, period);
                throw new CustomSystemException(HttpStatus.BAD_REQUEST, "Invalid period: must be 'daily', 'weekly', or 'monthly'");
            }
            if (startDate == null) startDate = LocalDate.now().minusDays(30);
            if (endDate == null) endDate = LocalDate.now();

            AdminDashboardDTO dto = new AdminDashboardDTO();

            // Staff Activity
            List<Object[]> staffActivity = purchaseDAO.findStaffActivity(startDate, endDate);
            dto.setStaffActivity(staffActivity.stream()
                    .map(row -> new AdminDashboardDTO.StaffActivity(
                            (String) row[0],
                            (String) row[1],
                            (String) row[2],
                            ((Number) row[3]).longValue()
                    ))
                    .collect(Collectors.toList()));

            // Customer Purchase Trends
            List<Object[]> customerTrends = customerDAO.findCustomerPurchaseTrends(startDate, endDate);
            dto.setCustomerPurchaseTrends(customerTrends.stream()
                    .map(row -> new AdminDashboardDTO.CustomerTrend(
                            ((Number) row[0]).longValue(),
                            (String) row[1],
                            (String) row[2],
                            ((Number) row[3]).longValue(),
                            ((Number) row[4]).doubleValue()
                    ))
                    .collect(Collectors.toList()));

            // Loyalty Points Usage
            List<Object[]> loyaltyPoints = loyaltyPointsDAO.findLoyaltyPointsUsage(startDate, endDate);
            dto.setLoyaltyPointsUsage(loyaltyPoints.stream()
                    .map(row -> new AdminDashboardDTO.LoyaltyPointsUsage(
                            ((Number) row[0]).longValue(),
                            (String) row[1],
                            ((Number) row[2]).longValue(),
                            ((Number) row[3]).longValue()
                    ))
                    .collect(Collectors.toList()));

            // Coupon Discount Impact
            List<Object[]> couponImpact = purchaseDAO.findCouponDiscountImpact(startDate, endDate);
            dto.setCouponDiscountImpact(couponImpact.stream()
                    .map(row -> new AdminDashboardDTO.CouponDiscount(
                            (String) row[0],
                            ((Number) row[1]).doubleValue(),
                            ((Number) row[2]).longValue()
                    ))
                    .collect(Collectors.toList()));

            // Total Counts
            dto.setTotalCustomerCount(customerDAO.count());
            dto.setTotalStaffCount(staffDAO.countActiveStaff());
            dto.setTotalProductCount(productDAO.count());
            dto.setTotalCategoryCount(categoryDAO.count());
            dto.setTotalBrandCount(brandDAO.count());

            log.info("✅ {} => getAdminDashboard() => Subject: retrieved admin dashboard || username: {}", DashboardServiceImpl, username);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, ValueConstant.RETRIEVE, dto);
        } catch (Exception e) {
            log.error("{} => getAdminDashboard() => Unexpected Error: {}", DashboardServiceImpl, e.getMessage());
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<ManagerDashboardDTO>> getManagerDashboard(String username, LocalDate startDate, LocalDate endDate) throws CustomSystemException {
        log.info("{} => getManagerDashboard() => Subject: retrieving manager dashboard ||| username: {}", DashboardServiceImpl, username);
        try {
            userUtils.getUserByUserName(username);
            if (startDate == null) startDate = LocalDate.now().minusDays(30);
            if (endDate == null) endDate = LocalDate.now();

            ManagerDashboardDTO dto = new ManagerDashboardDTO();

            // Revenue Over Time
            List<Object[]> revenueOverTime = purchaseDAO.findRevenueOverTime("monthly", startDate, endDate);
            dto.setRevenueOverTime(revenueOverTime.stream()
                    .map(row -> new ManagerDashboardDTO.RevenueOverTime(
                            (String) row[0],
                            ((Number) row[1]).doubleValue()
                    ))
                    .collect(Collectors.toList()));

            // Revenue by Category
            List<Object[]> revenueByCategory = purchaseDAO.findRevenueByCategory(startDate, endDate);
            dto.setRevenueByCategory(revenueByCategory.stream()
                    .map(row -> new ManagerDashboardDTO.RevenueByCategory(
                            (String) row[0],
                            ((Number) row[1]).doubleValue()
                    ))
                    .collect(Collectors.toList()));

            // Customer Demographics
            List<Object[]> demographics = customerDAO.findCustomerDemographics();
            dto.setCustomerDemographics(demographics.stream()
                    .map(row -> new ManagerDashboardDTO.Demographic(
                            (String) row[0],
                            ((Number) row[1]).longValue()
                    ))
                    .collect(Collectors.toList()));

            // Supplier Performance
            List<Object[]> supplierPerformance = supplierTransactionDAO.findSupplierPerformance(startDate, endDate);
            dto.setSupplierPerformance(supplierPerformance.stream()
                    .map(row -> new ManagerDashboardDTO.SupplierPerformance(
                            (String) row[0],
                            ((Number) row[1]).longValue(),
                            ((Number) row[2]).longValue()
                    ))
                    .collect(Collectors.toList()));

            // Top Selling Products
            List<Object[]> topProducts = productDAO.findTopSellingProducts(startDate, endDate);
            dto.setTopProducts(topProducts.stream()
                    .map(row -> new ManagerDashboardDTO.TopProduct(
                            ((Number) row[0]).longValue(),
                            (String) row[1],
                            ((Number) row[2]).longValue(),
                            ((Number) row[3]).doubleValue()
                    ))
                    .collect(Collectors.toList()));

            // Brand Popularity
            List<Object[]> brandPopularity = purchaseDAO.findBrandPopularity(startDate, endDate);
            dto.setBrandPopularity(brandPopularity.stream()
                    .map(row -> new ManagerDashboardDTO.BrandPopularity(
                            (String) row[0],
                            ((Number) row[1]).longValue(),
                            ((Number) row[2]).doubleValue()
                    ))
                    .collect(Collectors.toList()));

            // Loyalty Points Usage
            List<Object[]> loyaltyPoints = loyaltyPointsDAO.findLoyaltyPointsUsage(startDate, endDate);
            dto.setLoyaltyPointsUsage(loyaltyPoints.stream()
                    .map(row -> new ManagerDashboardDTO.LoyaltyPointsUsage(
                            ((Number) row[0]).longValue(),
                            (String) row[1],
                            ((Number) row[2]).longValue(),
                            ((Number) row[3]).longValue()
                    ))
                    .collect(Collectors.toList()));

            // Coupon Discount Impact
            List<Object[]> couponImpact = purchaseDAO.findCouponDiscountImpact(startDate, endDate);
            dto.setCouponDiscountImpact(couponImpact.stream()
                    .map(row -> new ManagerDashboardDTO.CouponDiscount(
                            (String) row[0],
                            ((Number) row[1]).doubleValue(),
                            ((Number) row[2]).longValue()
                    ))
                    .collect(Collectors.toList()));

            // Total Counts
            dto.setTotalProductCount(productDAO.count());
            dto.setTotalCategoryCount(categoryDAO.count());
            dto.setTotalBrandCount(brandDAO.count());
            dto.setTotalCustomerCount(customerDAO.count());
            dto.setTotalStaffCount(staffDAO.countActiveStaff());

            log.info("✅ {} => getManagerDashboard() => Subject: retrieved manager dashboard || username: {}", DashboardServiceImpl, username);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, ValueConstant.RETRIEVE, dto);
        } catch (Exception e) {
            log.error("{} => getManagerDashboard() => Unexpected Error: {}", DashboardServiceImpl, e.getMessage());
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }
}
