package com.Cloudy.Cloudy_Self_Checkout_POS.serviceImpl;

import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.*;
import com.Cloudy.Cloudy_Self_Checkout_POS.Reports.*;
import com.Cloudy.Cloudy_Self_Checkout_POS.constants.ValueConstant;
import com.Cloudy.Cloudy_Self_Checkout_POS.customException.CustomSystemException;
import com.Cloudy.Cloudy_Self_Checkout_POS.dao.*;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.ProductDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.SupplierTransactionDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.loyaltyPointsHistoryDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.service.ReportsService;
import com.Cloudy.Cloudy_Self_Checkout_POS.utils.CloudyUtils;
import com.Cloudy.Cloudy_Self_Checkout_POS.utils.UserUtils;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ListWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class ReportsServiceImpl implements ReportsService {
    private final SupplierTransactionDAO transactionDAO;
    private final CategoryDao categoryDao;
    private final BrandDAO brandDAO;
    private final SupplierDao supplierDao;
    private final CustomerDao customerDao;
    private final CouponDao couponDao;
    private final ProductDao productDao;
    private final PurchaseDao purchaseDao;
    private final StaffDao staffDao;
    private  final DiscountDAO discountDAO;
    private final  LoyaltyPointsDAO loyaltyPointsDAO;
    private final OtpDao otpAuthDao;
    private  final SupplierTransactionDAO supplierTransactionDAO;

    @Autowired
    private ObjectMapper objectMapper;

    private final UserUtils userUtils;

    private final String ReportsServiceImpl = "ReportsServiceImpl";
    private final String ReportServiceImpl = "ReportServiceImpl";

    @Override
    public ResponseEntity<ApiResponseWrapper<SupplierTransactionDTO>> purchaseTransactions(String username, SupplierTransactionRequest request) {
        log.info("{} => purchaseTransactions() => Subject:purchase Transactions from supplier ||| username: {}", ReportsServiceImpl, username);

        try {
            userUtils.getUserByUserName(username);

            Supplier supplier = supplierDao.findById(request.getSupplierId()).orElseThrow(() -> new CustomSystemException(HttpStatus.BAD_REQUEST,"Supplier ID not found"));
            Category category = categoryDao.findById(request.getCategoryId()).orElseThrow(() -> new CustomSystemException(HttpStatus.BAD_REQUEST,"Category ID not found"));
            Brand brand = brandDAO.findById(request.getBrandId()).orElseThrow(() -> new CustomSystemException(HttpStatus.BAD_REQUEST,"Brand ID not found"));
            Product product = productDao.findById(request.getProductId()).orElseThrow(()-> new CustomSystemException(HttpStatus.BAD_REQUEST,"Product ID not found"));

            SupplierTransaction transaction = SupplierTransaction.builder()
                    .guid(UUID.randomUUID().toString())
                            .supplier(supplier)
                                    .category(category)
                                            .brand(brand)
                                                    .quantity(request.getQuantity())
                                                            .transactionDate(LocalDateTime.now())
                    .products(new ArrayList<>())
                    .createdBy(username).build();

//            update product stock
            product.setStockUnit((int) (product.getStockUnit() + request.getQuantity()));
            product.setManuDate(request.getManuDate());
            product.setExpDate(request.getExpDate());
            product.setSupplierTransaction(transaction);

            transaction.getProducts().add(product);
            transactionDAO.save(transaction);
            productDao.save(product);



            SupplierTransactionDTO dto = SupplierTransactionDTO.builder()
                    .supplierId(transaction.getSupplier().getId())
                    .guid(transaction.getGuid())
                    .transactionId(transaction.getTransactionId())
                    .brandId(transaction.getBrand().getId())
                    .categoryId(transaction.getCategory().getCategoryId())
                    .product(Arrays.asList(mapToProductDTO(transaction.getProducts().get(0))))
                    .transactionDate(transaction.getTransactionDate())
                    .quantity((int) transaction.getQuantity())
                    .createdBy(transaction.getCreatedBy())
                    .build();





            log.info("✅ {} =>purchaseTransactions() => Subject: purchaseTransactions from supplier || username: {}", ReportsServiceImpl, username);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, ValueConstant.ADD, dto);

        } catch (Exception e) {
            log.error("{} => purchaseTransactions() => Subject: purchaseTransactions from supplier => Unexpected Error: ", ReportsServiceImpl, e);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<ListWrapper<SupplierBrandCategoryReport>>> generateSupplierReports(
            String username, int page, int size) throws CustomSystemException {

        log.info("{} => generateSupplierReports() => Subject: Generate Supplier Reports ||| username: {}",
                ReportsServiceImpl, username);

        try {
            // Validate user
            userUtils.getUserByUserName(username);

            // Fetch all transactions
            List<SupplierTransaction> transactions = transactionDAO.findAll();

            // Group transactions by brand and category to calculate total quantities
            Map<String, Double> totalQuantitiesByBrandAndCategory = transactions.stream()
                    .flatMap(t -> t.getProducts().stream()) // Flatten the list of products
                    .collect(Collectors.groupingBy(
                            p -> p.getBrand().getName() + "-" + p.getCategory().getCategoryName(),
                            Collectors.summingDouble(p -> p.getStockUnit() != null ? p.getStockUnit() : 0)
                    ));

            // Calculate import percentages for each supplier-brand-category combination
            List<SupplierBrandCategoryReport> reports = transactions.stream()
                    .flatMap(t -> t.getProducts().stream()) // Flatten the list of products
                    .collect(Collectors.groupingBy(
                            p -> p.getSupplierTransaction().getSupplier().getId() + "-" +
                                    p.getBrand().getName() + "-" +
                                    p.getCategory().getCategoryName(),
                            Collectors.summingDouble(p -> p.getStockUnit() != null ? p.getStockUnit() : 0)
                    ))
                    .entrySet()
                    .stream()
                    .map(entry -> {
                        String[] keys = entry.getKey().split("-");
                        Long supplierId = Long.parseLong(keys[0]);
                        String supplierName = getSupplierNameById(supplierId); // Helper method to fetch supplier name
                        String brandName = keys[1];
                        String categoryName = keys[2];
                        Double supplierQuantity = entry.getValue();
                        Double totalQuantity = totalQuantitiesByBrandAndCategory.getOrDefault(brandName + "-" + categoryName, 0.0);

                        Double importPercentage = totalQuantity > 0 ? (supplierQuantity / totalQuantity) * 100 : 0.0;

                        return SupplierBrandCategoryReport.builder()
                                .supplierId(supplierId)
                                .supplierName(supplierName)
                                .brandId(getBrandIdByName(brandName)) // Helper method to fetch brand ID
                                .brandName(brandName)
                                .categoryId(getCategoryIdByName(categoryName)) // Helper method to fetch category ID
                                .categoryName(categoryName)
                                .importPercentage(importPercentage)
                                .isActive(isActive(supplierName, brandName, categoryName, transactions))
                                .build();
                    })
                    .sorted(Comparator.comparing(SupplierBrandCategoryReport::getImportPercentage).reversed()) // Sort by import percentage
                    .skip((long) page * size) // Pagination: Skip records
                    .limit(size) // Pagination: Limit records
                    .toList();

            // Count total records for pagination metadata
            long totalRecords = transactions.stream()
                    .flatMap(t -> t.getProducts().stream()) // Flatten the list of products
                    .map(p -> p.getSupplierTransaction().getSupplier().getId() + "-" +
                            p.getBrand().getName() + "-" +
                            p.getCategory().getCategoryName())
                    .distinct()
                    .count();

            // Create paginated response
            ListWrapper<SupplierBrandCategoryReport> reportList = new ListWrapper<>(reports, (int) totalRecords);

            log.info("✅ {} => generateSupplierReports() => Subject: Generate Supplier Reports || username: {}",
                    ReportsServiceImpl, username);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, ValueConstant.ADD, reportList);

        } catch (Exception e) {
            log.error("{} => generateSupplierReports() => Subject: Generate Supplier Reports => Unexpected Error: ",
                    ReportsServiceImpl, e);
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }
    @Override
    public ResponseEntity<ApiResponseWrapper<ListWrapper<ProductSupplierReport>>> generateProductSuppliersReport(
            String username, int page, int size) throws CustomSystemException {

        log.info("{} => generateProductSuppliersReport() => Subject: Generate Product Suppliers Report ||| username: {}",
                ReportsServiceImpl, username);

        try {
            // Validate user
            userUtils.getUserByUserName(username);

            // Fetch all transactions
            List<SupplierTransaction> transactions = transactionDAO.findAll();

            // Flatten the list of products from all transactions
            Map<String, List<SupplierTransaction>> productsByGuid = transactions.stream()
                    .flatMap(t -> t.getProducts().stream()) // Flatten the list of products
                    .collect(Collectors.groupingBy(
                            p -> p.getProductGuid(), // Group by product GUID
                            Collectors.mapping(Product::getSupplierTransaction, Collectors.toList())
                    ));

            // Filter products supplied by multiple suppliers
            // Only include products with multiple suppliers
            // Aggregate data for the product
            // Sort by total quantity
            // Pagination: Skip records
            // Pagination: Limit records
            List<ProductSupplierReport> toSort = new ArrayList<>();
            for (Map.Entry<String, List<SupplierTransaction>> stringListEntry : productsByGuid.entrySet()) {
                if (stringListEntry.getValue().size() > 1) {
                    String productGuid = stringListEntry.getKey();
                    List<SupplierTransaction> supplierTransactions = stringListEntry.getValue();

                    // Aggregate data for the product
                    Product firstProduct = supplierTransactions.get(0).getProducts().stream()
                            .filter(p -> p.getProductGuid().equals(productGuid))
                            .findFirst()
                            .orElseThrow(() -> new CustomSystemException(HttpStatus.BAD_REQUEST, "Product not found"));

                    ProductSupplierReport productNotFound = ProductSupplierReport.builder()
                            .productGuid(productGuid)
                            .productName(firstProduct.getProductName())
                            .brandName(firstProduct.getBrand().getName())
                            .categoryName(firstProduct.getCategory().getCategoryName())
                            .totalQuantity(supplierTransactions.stream()
                                    .flatMap(t -> t.getProducts().stream())
                                    .filter(p -> p.getProductGuid().equals(productGuid))
                                    .mapToInt(p -> p.getStockUnit() != null ? p.getStockUnit() : 0)
                                    .sum())
                            .suppliers(supplierTransactions.stream()
                                    .map(t -> t.getSupplier().getName())
                                    .distinct()
                                    .toList())
                            .transactions(supplierTransactions.stream()
                                    .map(t -> ProductSupplierTransaction.builder()
                                            .transactionId(t.getTransactionId())
                                            .supplierName(t.getSupplier().getName())
                                            .quantity(t.getProducts().stream()
                                                    .filter(p -> p.getProductGuid().equals(productGuid))
                                                    .mapToInt(p -> p.getStockUnit() != null ? p.getStockUnit() : 0)
                                                    .sum())
                                            .transactionDate(t.getTransactionDate())
                                            .build())
                                    .toList())
                            .build();
                    toSort.add(productNotFound);
                }
            }
            toSort.sort(Comparator.comparing(ProductSupplierReport::getTotalQuantity).reversed());
            List<ProductSupplierReport> reports = new ArrayList<>();
            long limit = size;
            long toSkip = (long) page * size;
            for (ProductSupplierReport productNotFound : toSort) {
                if (toSkip > 0) {
                    toSkip--;
                    continue;
                }
                if (limit-- == 0) break;
                reports.add(productNotFound);
            }

            // Count total records for pagination metadata
            long totalRecords = productsByGuid.entrySet().stream()
                    .filter(entry -> entry.getValue().size() > 1) // Only count products with multiple suppliers
                    .count();

            // Create paginated response
            ListWrapper<ProductSupplierReport> reportList = new ListWrapper<>(reports, (int) totalRecords);

            log.info("✅ {} => generateProductSuppliersReport() => Subject: Generate Product Suppliers Report || username: {}",
                    ReportsServiceImpl, username);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, ValueConstant.ADD, reportList);

        } catch (Exception e) {
            log.error("{} => generateProductSuppliersReport() => Subject: Generate Product Suppliers Report => Unexpected Error: ",
                    ReportsServiceImpl, e);
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<List<loyaltyPointsHistoryDTO>>> getLoyaltyPointsTransactionsHistory(String username) {
        log.info("{} => getLoyaltyPointsTransactionHistory() => Subject: Get loyalty points transaction history || Username: {}",
                ReportsServiceImpl, username);

        try {
            User user = userUtils.getUserByUsernameOptional(username);


            // Fetch the customer by GUID
            Customer customer = customerDao.findById(user.getUserId())
                    .orElseThrow(() -> new CustomSystemException(HttpStatus.NOT_FOUND,ValueConstant.BAD_CREDENTIALS));

            // Fetch loyalty points transactions for the customer
            List<loyaltyPointsHistoryDTO> loyaltyPointsTransactions = loyaltyPointsDAO.findByUserIdOrderByTransactionDateDesc(user.getUserId());

            log.info("✅ {} => getLoyaltyPointsTransactionHistory() => Successfully fetched {} transactions for username: {}",
                    ReportsServiceImpl, loyaltyPointsTransactions.size(),username);

            // Return the response
            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, "Transaction history retrieved successfully", loyaltyPointsTransactions);

        } catch (CustomSystemException e) {
            log.error("{} => getLoyaltyPointsTransactionHistory() => Error: {}",
                    ReportsServiceImpl, e.getMessage());
            return CloudyUtils.getResponseEntityCustom(HttpStatus.BAD_REQUEST, e.getMessage());

        } catch (Exception e) {
            log.error("{} => getLoyaltyPointsTransactionHistory() => Unexpected Error: {}",
                    ReportsServiceImpl, e.getMessage());
            return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong while fetching transaction history");
        }
    }
    @Override
    public ResponseEntity<ApiResponseWrapper<ListWrapper<RevenueOverTimeReport>>> getRevenueOverTime(String username, String period, LocalDate startDate, LocalDate endDate) throws CustomSystemException {
        log.info("{} => getRevenueOverTime() => Subject: retrieving revenue over time ||| username: {}", ReportServiceImpl, username);
        try {
            userUtils.getUserByUserName(username);
            List<Object[]> revenueData = purchaseDao.findRevenueOverTime(period, startDate, endDate);
            List<RevenueOverTimeReport> reports = revenueData.stream().map(row ->
                    RevenueOverTimeReport.builder()
                            .period(String.valueOf( row[0]))
                            .totalRevenue((BigDecimal) row[1])
                            .build()
            ).collect(Collectors.toList());
            ListWrapper<RevenueOverTimeReport> wrapper = new ListWrapper<>(reports,reports.size());
            log.info("✅ {} => getRevenueOverTime() => Subject: retrieved revenue over time || username: {}", ReportServiceImpl, username);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, ValueConstant.RETRIEVE, wrapper);
        } catch (Exception e) {
            log.error("{} => getRevenueOverTime() => Unexpected Error: {}", ReportServiceImpl, e.getMessage());
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<ListWrapper<RevenueByCategoryReport>>> getRevenueByCategory(String username, LocalDate startDate, LocalDate endDate) throws CustomSystemException {
        log.info("{} => getRevenueByCategory() => Subject: retrieving revenue by category ||| username: {}", ReportServiceImpl, username);
        try {
            userUtils.getUserByUserName(username);
            List<Object[]> revenueData = purchaseDao.findRevenueByCategory(startDate, endDate);
            List<RevenueByCategoryReport> reports = revenueData.stream().map(row ->
                    RevenueByCategoryReport.builder()
                            .categoryName((String) row[0])
                            .totalRevenue((BigDecimal) row[1])
                            .build()
            ).collect(Collectors.toList());
            ListWrapper<RevenueByCategoryReport> wrapper = new ListWrapper<>(reports,reports.size());
            log.info("✅ {} => getRevenueByCategory() => Subject: retrieved revenue by category || username: {}", ReportServiceImpl, username);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, ValueConstant.RETRIEVE, wrapper);
        } catch (Exception e) {
            log.error("{} => getRevenueByCategory() => Unexpected Error: {}", ReportServiceImpl, e.getMessage());
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<ListWrapper<CouponDiscountReport>>> getCouponDiscountImpact(String username, LocalDate startDate, LocalDate endDate) throws CustomSystemException {
        log.info("{} => getCouponDiscountImpact() => Subject: retrieving coupon discount impact ||| username: {}", ReportServiceImpl, username);
        try {
            userUtils.getUserByUserName(username);
            List<Object[]> couponData = purchaseDao.findCouponDiscountImpact(startDate, endDate);
            List<CouponDiscountReport> reports = couponData.stream().map(row ->
                    CouponDiscountReport.builder()
                            .couponCode((String) row[0])
                            .totalDiscount((BigDecimal) row[1])
                            .usageCount(((Number) row[2]).longValue())
                            .build()
            ).collect(Collectors.toList());
            ListWrapper<CouponDiscountReport> wrapper = new ListWrapper<>(reports, reports.size());
            log.info("✅ {} => getCouponDiscountImpact() => Subject: retrieved coupon discount impact || username: {}", ReportServiceImpl, username);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, ValueConstant.RETRIEVE, wrapper);
        } catch (Exception e) {
            log.error("{} => getCouponDiscountImpact() => Unexpected Error: {}", ReportServiceImpl, e.getMessage());
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<ListWrapper<CustomerPurchaseReport>>> getCustomerPurchaseTrends(String username, LocalDate startDate, LocalDate endDate) throws CustomSystemException {
        log.info("{} => getCustomerPurchaseTrends() => Subject: retrieving customer purchase trends ||| username: {}", ReportServiceImpl, username);
        try {
            userUtils.getUserByUserName(username);
            List<Object[]> customerData = customerDao.findCustomerPurchaseTrends(startDate, endDate);
            List<CustomerPurchaseReport> reports = customerData.stream().map(row ->
                    CustomerPurchaseReport.builder()
                            .customerId(((Number) row[0]).longValue())
                            .firstName((String) row[1])
                            .lastName((String) row[2])
                            .purchaseCount(((Number) row[3]).longValue())
                            .totalSpent((BigDecimal) row[4])
                            .build()
            ).collect(Collectors.toList());
            ListWrapper<CustomerPurchaseReport> wrapper = new ListWrapper<>(reports,reports.size());
            log.info("✅ {} => getCustomerPurchaseTrends() => Subject: retrieved customer purchase trends || username: {}", ReportServiceImpl, username);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, ValueConstant.RETRIEVE, wrapper);
        } catch (Exception e) {
            log.error("{} => getCustomerPurchaseTrends() => Unexpected Error: {}", ReportServiceImpl, e.getMessage());
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<ListWrapper<LoyaltyPointsReport>>> getLoyaltyPointsUsage(String username, LocalDate startDate, LocalDate endDate) throws CustomSystemException {
        log.info("{} => getLoyaltyPointsUsage() => Subject: retrieving loyalty points usage ||| username: {}", ReportServiceImpl, username);
        try {
            userUtils.getUserByUserName(username);
            List<Object[]> loyaltyData = loyaltyPointsDAO.findLoyaltyPointsUsage(startDate, endDate);
            List<LoyaltyPointsReport> reports = loyaltyData.stream().map(row ->
                    LoyaltyPointsReport.builder()
                            .customerId(((Number) row[0]).longValue())
                            .firstName((String) row[1])
                            .pointsEarned(((Number) row[2]).intValue())
                            .pointsRedeemed(((Number) row[3]).intValue())
                            .build()
            ).collect(Collectors.toList());
            ListWrapper<LoyaltyPointsReport> wrapper = new ListWrapper<>(reports,reports.size());
            log.info("✅ {} => getLoyaltyPointsUsage() => Subject: retrieved loyalty points usage || username: {}", ReportServiceImpl, username);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, ValueConstant.RETRIEVE, wrapper);
        } catch (Exception e) {
            log.error("{} => getLoyaltyPointsUsage() => Unexpected Error: {}", ReportServiceImpl, e.getMessage());
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<ListWrapper<CustomerDemographicsReport>>> getCustomerDemographics(String username) throws CustomSystemException {
        log.info("{} => getCustomerDemographics() => Subject: retrieving customer demographics ||| username: {}", ReportServiceImpl, username);
        try {
            userUtils.getUserByUserName(username);
            List<Object[]> demographicData = customerDao.findCustomerDemographics();
            List<CustomerDemographicsReport> reports = demographicData.stream().map(row ->
                    CustomerDemographicsReport.builder()
                            .ageGroup((String) row[0])
                            .customerCount(((Number) row[1]).longValue())
                            .build()
            ).collect(Collectors.toList());
            ListWrapper<CustomerDemographicsReport> wrapper = new ListWrapper<>(reports,reports.size());
            log.info("✅ {} => getCustomerDemographics() => Subject: retrieved customer demographics || username: {}", ReportServiceImpl, username);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, ValueConstant.RETRIEVE, wrapper);
        } catch (Exception e) {
            log.error("{} => getCustomerDemographics() => Unexpected Error: {}", ReportServiceImpl, e.getMessage());
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<ListWrapper<TopSellingProductReport>>> getTopSellingProducts(String username, LocalDate startDate, LocalDate endDate) throws CustomSystemException {
        log.info("{} => getTopSellingProducts() => Subject: retrieving top selling products ||| username: {}", ReportServiceImpl, username);
        try {
            userUtils.getUserByUserName(username);
            List<Object[]> productData = productDao.findTopSellingProducts(startDate, endDate);
            List<TopSellingProductReport> reports = productData.stream().map(row ->
                    TopSellingProductReport.builder()
                            .productId(((Number) row[0]).longValue())
                            .productName((String) row[1])
                            .quantitySold((int) ((Number) row[2]).longValue())
                            .totalRevenue((BigDecimal) row[3])
                            .build()
            ).collect(Collectors.toList());
            ListWrapper<TopSellingProductReport> wrapper = new ListWrapper<>(reports,reports.size());
            log.info("✅ {} => getTopSellingProducts() => Subject: retrieved top selling products || username: {}", ReportServiceImpl, username);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, ValueConstant.RETRIEVE, wrapper);
        } catch (Exception e) {
            log.error("{} => getTopSellingProducts() => Unexpected Error: {}", ReportServiceImpl, e.getMessage());
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<ListWrapper<StockLevelReport>>> getStockLevelsByCategory(String username) throws CustomSystemException {
        log.info("{} => getStockLevelsByCategory() => Subject: retrieving stock levels by category ||| username: {}", ReportServiceImpl, username);
        try {
            userUtils.getUserByUserName(username);
            List<Object[]> stockData = productDao.findStockLevelsByCategory();

            // Log the raw data for debugging
            log.info("Raw stock data: {}", stockData);

            List<StockLevelReport> reports = stockData.stream()
                    .map(row -> {
                        try {
                            String json = (String) row[5]; // lowStockProductsDetails
                            log.info("lowStockProductsDetails JSON for category {}: {}", row[0], json); // Debug log
                            List<StockLevelReport.LowStockProduct> lowStockProducts = json != null && !json.equals("[]")
                                    ? objectMapper.readValue(json, objectMapper.getTypeFactory()
                                    .constructCollectionType(List.class, StockLevelReport.LowStockProduct.class))
                                    : List.of();
                            return StockLevelReport.builder()
                                    .categoryId(((Number) row[0]).longValue())
                                    .categoryName(row[1] != null ? (String) row[1] : "")
                                    .totalProducts((int) row[2])
                                    .totalStockUnits((int) row[3])
                                    .lowStockProducts((int) row[4])
                                    .lowStockProductsDetails(lowStockProducts)
                                    .build();
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to parse lowStockProductsDetails JSON", e);
                        }
                    })
                    .collect(Collectors.toList());
            ListWrapper<StockLevelReport> wrapper = new ListWrapper<>(reports, reports.size());
            log.info("✅ {} => getStockLevelsByCategory() => Subject: retrieved stock levels by category || username: {}", ReportServiceImpl, username);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, ValueConstant.RETRIEVE, wrapper);
        } catch (Exception e) {
            log.error("{} => getStockLevelsByCategory() => Unexpected Error: {}", ReportServiceImpl, e.getMessage());
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }
    @Override
    public ResponseEntity<ApiResponseWrapper<ListWrapper<ProductExpiryReport>>> getProductExpiryAlerts(String username, int daysThreshold) throws CustomSystemException {
        log.info("{} => getProductExpiryAlerts() => Subject: retrieving product expiry alerts ||| username: {}", ReportServiceImpl, username);
        try {
            userUtils.getUserByUserName(username);
            List<Object[]> expiryData = productDao.findProductExpiryAlerts(daysThreshold);
            List<ProductExpiryReport> reports = expiryData.stream().map(row ->
                    ProductExpiryReport.builder()
                            .productId(((Number) row[0]).longValue())
                            .productName((String) row[1])
                            .barcode((String) row[2])
                            .expiryDate(((java.sql.Date) row[3]).toLocalDate()) // Convert java.sql.Date to LocalDate
                            .stockUnit(((Number) row[4]).intValue())
                            .build()
            ).collect(Collectors.toList());
            ListWrapper<ProductExpiryReport> wrapper = new ListWrapper<>(reports, reports.size());
            log.info("✅ {} => getProductExpiryAlerts() => Subject: retrieved product expiry alerts || username: {}", ReportServiceImpl, username);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, ValueConstant.RETRIEVE, wrapper);
        } catch (Exception e) {
            log.error("{} => getProductExpiryAlerts() => Unexpected Error: {}", ReportServiceImpl, e.getMessage(), e);
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<ListWrapper<SupplierPerformanceReport>>> getSupplierPerformance(String username, LocalDate startDate, LocalDate endDate) throws CustomSystemException {
        log.info("{} => getSupplierPerformance() => Subject: retrieving supplier performance ||| username: {}", ReportServiceImpl, username);
        try {
            userUtils.getUserByUserName(username);
            List<Object[]> supplierData = supplierTransactionDAO.findSupplierPerformance(startDate, endDate);
            List<SupplierPerformanceReport> reports = supplierData.stream().map(row ->
                    SupplierPerformanceReport.builder()
                            .supplierName((String) row[0])
                            .totalQuantity(((Number) row[1]).doubleValue())
                            .transactionCount(((Number) row[2]).longValue())
                            .build()
            ).collect(Collectors.toList());
            ListWrapper<SupplierPerformanceReport> wrapper = new ListWrapper<>(reports,reports.size());
            log.info("✅ {} => getSupplierPerformance() => Subject: retrieved supplier performance || username: {}", ReportServiceImpl, username);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, ValueConstant.RETRIEVE, wrapper);
        } catch (Exception e) {
            log.error("{} => getSupplierPerformance() => Unexpected Error: {}", ReportServiceImpl, e.getMessage());
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<ListWrapper<BrandPopularityReport>>> getBrandPopularity(String username, LocalDate startDate, LocalDate endDate) throws CustomSystemException {
        log.info("{} => getBrandPopularity() => Subject: retrieving brand popularity ||| username: {}", ReportServiceImpl, username);
        try {
            userUtils.getUserByUserName(username);
            List<Object[]> brandData = purchaseDao.findBrandPopularity(startDate, endDate);
            List<BrandPopularityReport> reports = brandData.stream().map(row ->
                    BrandPopularityReport.builder()
                            .brandName((String) row[0])
                            .quantitySold((int) ((Number) row[1]).longValue())
                            .totalRevenue((BigDecimal) row[2])
                            .build()
            ).collect(Collectors.toList());
            ListWrapper<BrandPopularityReport> wrapper = new ListWrapper<>(reports,reports.size());
            log.info("✅ {} => getBrandPopularity() => Subject: retrieved brand popularity || username: {}", ReportServiceImpl, username);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, ValueConstant.RETRIEVE, wrapper);
        } catch (Exception e) {
            log.error("{} => getBrandPopularity() => Unexpected Error: {}", ReportServiceImpl, e.getMessage());
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<ListWrapper<StaffActivityReport>>> getStaffActivity(String username, LocalDate startDate, LocalDate endDate) throws CustomSystemException {
        log.info("{} => getStaffActivity() => Subject: retrieving staff activity ||| username: {}", ReportServiceImpl, username);
        try {
            userUtils.getUserByUserName(username);
            List<Object[]> staffData = purchaseDao.findStaffActivity(startDate, endDate);
            List<StaffActivityReport> reports = staffData.stream().map(row ->
                    StaffActivityReport.builder()
                            .staffId(((Number) row[0]).longValue())
                            .firstName((String) row[1])
                            .lastName((String) row[2])
                            .transactionCount(((Number) row[3]).longValue())
                            .build()
            ).collect(Collectors.toList());
            ListWrapper<StaffActivityReport> wrapper = new ListWrapper<>(reports,reports.size());
            log.info("✅ {} => getStaffActivity() => Subject: retrieved staff activity || username: {}", ReportServiceImpl, username);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, ValueConstant.RETRIEVE, wrapper);
        } catch (Exception e) {
            log.error("{} => getStaffActivity() => Unexpected Error: {}", ReportServiceImpl, e.getMessage());
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<ListWrapper<OtpFailureReport>>> getOtpFailureAlerts(String username) throws CustomSystemException {
        log.info("{} => getOtpFailureAlerts() => Subject: retrieving OTP failure alerts ||| username: {}", ReportServiceImpl, username);
        try {
            userUtils.getUserByUserName(username);
            List<Object[]> otpData = otpAuthDao.findOtpFailureAlerts();
            List<OtpFailureReport> reports = otpData.stream().map(row ->
                    OtpFailureReport.builder()
                            .userId(((Number) row[0]).longValue())
                            .email((String) row[1])
                            .failedAttempts(((Number) row[2]).intValue())
                            .lockedUntil((Long) row[3])
                            .build()
            ).collect(Collectors.toList());
            ListWrapper<OtpFailureReport> wrapper = new ListWrapper<>(reports,reports.size());
            log.info("✅ {} => getOtpFailureAlerts() => Subject: retrieved OTP failure alerts || username: {}", ReportServiceImpl, username);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, ValueConstant.RETRIEVE, wrapper);
        } catch (Exception e) {
            log.error("{} => getOtpFailureAlerts() => Unexpected Error: {}", ReportServiceImpl, e.getMessage());
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    ////////////////////////////////////////////////////////////////////
    private Boolean isActive(String supplierName, String brandName, String categoryName, List<SupplierTransaction> transactions) {
        return transactions.stream()
                .filter(t -> t.getSupplier().getName().equals(supplierName))
                .filter(t -> t.getBrand().getName().equals(brandName))
                .filter(t -> t.getCategory().getCategoryName().equals(categoryName))
                .anyMatch(t -> t.getTransactionDate().isAfter(LocalDate.now().minusMonths(6).atStartOfDay())); // Active if transactions in the last 6 months
    }

    // Helper method to fetch supplier name by ID
    private String getSupplierNameById(Long supplierId) {
        return supplierDao.findById(supplierId)
                .map(Supplier::getName)
                .orElse("Unknown Supplier");
    }

    // Helper method to fetch brand ID by name
    private Long getBrandIdByName(String brandName) {
        return brandDAO.findIdByName(brandName);
    }

    // Helper method to fetch category ID by name
    private Long getCategoryIdByName(String categoryName) {
        return categoryDao.findIdByName(categoryName);
    }

    private ProductDTO mapToProductDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setTransactionId(product.getSupplierTransaction().getTransactionId());
        dto.setProductId(product.getProductId());
        dto.setBrand(product.getBrand().getName());
        dto.setBrandId(product.getBrand().getId());
        dto.setCategoryId(product.getCategory().getCategoryId());
        dto.setCategory(product.getCategory().getCategoryName());
        dto.setBarcode(product.getBarcode());
        dto.setManuDate(product.getManuDate());
        dto.setExpDate(product.getExpDate());
        return dto;
    }

}
