package com.Cloudy.Cloudy_Self_Checkout_POS.serviceImpl;

import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.*;
import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.Transaction;
import com.Cloudy.Cloudy_Self_Checkout_POS.constants.TransactionType;
import com.Cloudy.Cloudy_Self_Checkout_POS.constants.ValueConstant;
import com.Cloudy.Cloudy_Self_Checkout_POS.customException.CustomSystemException;
import com.Cloudy.Cloudy_Self_Checkout_POS.dao.*;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.CartItemDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.CheckoutDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.service.CheckoutService;
import com.Cloudy.Cloudy_Self_Checkout_POS.utils.CloudyUtils;
import com.Cloudy.Cloudy_Self_Checkout_POS.utils.UserUtils;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Slf4j
@Service
public class CheckoutServiceImpl implements CheckoutService {

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private APIContext apiContext;

    private final UserDao userDao;
    private final LoyaltyPointsDAO loyaltyPointsDAO;
    @Autowired
    private CouponDao couponDao;
    private final ProductDao productDao;
    private final TransactionDAO transactionDAO;
    private final PurchaseDao purchaseDao;
    private final PurchaseItemDAO purchaseItemDAO;
    private final CouponServiceImpl couponServiceImpl;

    private final Random random = new Random();
    private static final double TAX_RATE = 0.3; // 3% tax rate
    private static final double FAILURE_CHANCE = 0.2;
    private static final String CHECKOUT_SERVICE_IMPL = "CheckoutServiceImpl";
    private static final String CURRENCY = "USD";
    private static final String PAYMENT_METHOD = "PayPal";
    private static final int POINTS_PER_AMOUNT = 10;

    @Transactional
    @Override
    public ResponseEntity<ApiResponseWrapper<CheckoutDTO>> initiatePayment(CheckoutDTO request, String username) {
        log.info("{} => initiatePayment() => Subject: Initiate Payment ||| username: {}", CHECKOUT_SERVICE_IMPL, username);

        try {

            Coupon coupon = applyCoupon(request);

            // Validate the incoming CheckoutDTO against business rules
            validateCheckOutDTO(request, username);

            // Recalculate amounts on the backend for validation
            double backendSubtotal = calculateSubtotal(request);
            double backendTaxAmount = backendSubtotal * TAX_RATE;
            double backendFinalAmount = backendSubtotal + backendTaxAmount;
            double frontendFinalAmount = request.getFinalAmount();

            // Validate that frontend and backend calculations match
            if (Math.abs(backendFinalAmount - frontendFinalAmount) > 0.01) { // Tolerance of 0.01 for floating-point precision
                throw new CustomSystemException(HttpStatus.BAD_REQUEST,
                        "Amount mismatch: Frontend final amount (" + frontendFinalAmount +
                                ") does not match backend calculated final amount (" + backendFinalAmount + ")");
            }

            // Calculate discount amount for response
            double discountAmount = request.getOriginalAmount() - request.getTotalAmount();
            request.setDiscountAmount(discountAmount);

            // Set calculated tax and final amount in the request
            request.setTaxAmount(backendTaxAmount);
            request.setFinalAmount(backendFinalAmount); // Override frontend's finalAmount with backend's calculation

            List<Product> products = validateAndGetProducts(request.getCartItems());
            User user = userUtils.getUserByUsernameOptional(username);

            Purchase purchase = saveOrUpdatePurchase(request, user, coupon, products);
            request.setPurchaseId(purchase.getPurchaseId());

            Payment createdPayment = createPaypalPayment(purchase, backendFinalAmount);
            String approvalUrl = extractApprovalUrl(createdPayment);

            Transaction transaction = createPendingTransaction(purchase, createdPayment.getId(),
                    BigDecimal.valueOf(backendFinalAmount), "PENDING", PAYMENT_METHOD, null);
            request.setPaymentId(createdPayment.getId());
            request.setTransactionStatus("PENDING");
            request.setApprovalURl(approvalUrl);

            CheckoutDTO dto = mapDto(request);
            log.info("✅ {} => {} => Subject: Initiate Payment successfully || username: {}",
                    CHECKOUT_SERVICE_IMPL, "initiatePayment()", username);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, ValueConstant.ADD, dto);

        } catch (CustomSystemException e) {
            log.error("{} => initiatePayment() => Subject: Initiate Payment => Custom Error: {}", CHECKOUT_SERVICE_IMPL, e.getMessage());
            request.setTransactionStatus("FAILED");
            request.setApprovalURl(null);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.BAD_REQUEST, e.getMessage(), request);
        } catch (Exception e) {
            log.error("{} => initiatePayment() => Subject: Initiate Payment => Unexpected Error: {}", CHECKOUT_SERVICE_IMPL, e.getMessage());
            request.setTransactionStatus("FAILED");
            request.setApprovalURl(null);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG, request);
        }
    }


// Execute Payment
    @Transactional
    @Override
    public ResponseEntity<ApiResponseWrapper<CheckoutDTO>> executePayment(CheckoutDTO checkoutDTO, String paymentId, String payerId, String username) {
        log.info("{} => executePayment() => Subject: Executing payment for purchaseId: {}, paymentId: {}",
                CHECKOUT_SERVICE_IMPL, checkoutDTO.getPurchaseId(), paymentId);

        Transaction tx = null;
        try {
            validateCheckOutDTO(checkoutDTO, username);
            Purchase purchase = getPurchaseById(checkoutDTO.getPurchaseId());

            tx = createPendingTransaction(purchase, paymentId, BigDecimal.valueOf(checkoutDTO.getFinalAmount()),
                    "PENDING", PAYMENT_METHOD, null);

            Payment executedPayment;
            try {
                executedPayment = executePaypalPayment(paymentId, payerId);
                log.info("✅ {} => {} => Subject :execute payment Purchase successfully",
                        CHECKOUT_SERVICE_IMPL, "executePayment()");
            } catch (Exception e) {
                failTransaction(tx, "PayPal execution failed: " + e.getMessage());
                throw new CustomSystemException(HttpStatus.BAD_REQUEST, "Payment execution failed: " + e.getMessage());
            }

            if (random.nextDouble() < FAILURE_CHANCE) {
                failTransaction(tx, "Insufficient funds in PayPal account");
                throw new CustomSystemException(HttpStatus.BAD_REQUEST, "Insufficient funds in PayPal account");
            }

            if ("approved".equals(executedPayment.getState())) {
                completeTransactionAndPurchase(tx, purchase, payerId, checkoutDTO, username);
                checkoutDTO.setDiscountAmount(checkoutDTO.getOriginalAmount() - checkoutDTO.getTotalAmount());
                return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, ValueConstant.ADD, checkoutDTO);
            } else {
                failTransaction(tx, "PayPal payment not approved");
                throw new CustomSystemException(HttpStatus.BAD_REQUEST, "PayPal payment not approved");
            }

        } catch (CustomSystemException e) {
            log.error("{} => executePayment() => Subject: Executing payment for purchaseId: {} => Custom Error: {}",
                    CHECKOUT_SERVICE_IMPL, checkoutDTO.getPurchaseId(), e.getMessage());
            checkoutDTO.setTransactionStatus("FAILED");
            return CloudyUtils.getResponseEntityCustom(HttpStatus.BAD_REQUEST, e.getMessage(), checkoutDTO);
        } catch (Exception e) {
            log.error("{} => executePayment() => Subject: Executing payment for purchaseId: {} => Unexpected Error: {}",
                    CHECKOUT_SERVICE_IMPL, checkoutDTO.getPurchaseId(), e.getMessage());
            if (tx != null) {
                failTransaction(tx, "Unexpected error: " + e.getMessage());
            }
            checkoutDTO.setTransactionStatus("FAILED");
            return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG, checkoutDTO);
        }
    }

    @Transactional
    @Override
    public ResponseEntity<ApiResponseWrapper<String>> cancelPurchase(Long purchaseId) {
        log.info("{} => cancelPurchase() => Subject: Cancelling purchase with purchaseId: {}", CHECKOUT_SERVICE_IMPL, purchaseId);

        try {
            Purchase purchase = getPurchaseById(purchaseId);
            if ("COMPLETED".equals(purchase.getStatus())) {
                throw new CustomSystemException(HttpStatus.BAD_REQUEST, "Cannot cancel a completed purchase");
            }

            restoreStock(purchase);
            purchase.setStatus("CANCELLED");
            purchaseDao.save(purchase);

            cancelPendingTransactions(purchase);
            log.info("✅ {} => {} => Subject :cancel Purchase successfully",
                    CHECKOUT_SERVICE_IMPL, "cancelPurchase()");

            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, "Purchase cancelled successfully", "CANCELLED");
        } catch (CustomSystemException e) {
            log.error("{} => cancelPurchase() => Subject: Cancelling purchase => Custom Error: {}", CHECKOUT_SERVICE_IMPL, e.getMessage());
            return CloudyUtils.getResponseEntityCustom(HttpStatus.BAD_REQUEST, e.getMessage(), null);
        } catch (Exception e) {
            log.error("{} => cancelPurchase() => Subject: Cancelling purchase => Unexpected Error: {}", CHECKOUT_SERVICE_IMPL, e.getMessage());
            return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG, null);
        }
    }
    @Override
    public Integer getLoyaltyPoints(String username) throws CustomSystemException {
        User user = userUtils.getUserByUsernameOptional(username);
        Integer totalPoints = loyaltyPointsDAO.calculateTotalPoints(user.getUserId());
        return totalPoints != null ? totalPoints : 0;
    }



    // Private Helper Methods

    private Purchase getPurchaseById(Long purchaseId) throws CustomSystemException {
        return purchaseDao.findById(purchaseId)
                .orElseThrow(() -> new CustomSystemException(HttpStatus.BAD_REQUEST, "Purchase not found"));
    }


    private void validateCheckOutDTO(CheckoutDTO dto, String username) throws CustomSystemException {
        log.info("{} => validateCheckOutDTO() => Subject: Validate checkout DTO", CHECKOUT_SERVICE_IMPL);
        try {
            if (dto.getFinalAmount() <= 0) {
                throw new CustomSystemException(HttpStatus.BAD_REQUEST, "Invalid payment amount: Final amount must be greater than zero");
            }

            if (dto.getCartItems() == null || dto.getCartItems().isEmpty()) {
                throw new CustomSystemException(HttpStatus.BAD_REQUEST, "Cart cannot be empty");
            }

            double subtotal = dto.getTotalAmount() - dto.getCouponDiscount() -
                    (dto.getLoyaltyPointsUsed() != null ? dto.getLoyaltyPointsUsed() : 0);
            double calculatedTax = subtotal * TAX_RATE;
            double calculatedFinal = subtotal + calculatedTax;

//            if (Math.abs(calculatedFinal - dto.getFinalAmount()) > 0.01) {
//                throw new CustomSystemException(HttpStatus.BAD_REQUEST,
//                        "Invalid payment amount: Total, discounts, loyalty points, and tax do not match final amount");
//            }

//            double cartTotal = dto.getCartItems().stream()
//                    .mapToDouble(item -> item.getDiscountedPrice() * item.getQuantity())
//                    .sum();
//            if (Math.abs(cartTotal - dto.getTotalAmount()) > 0.01) {
//                throw new CustomSystemException(HttpStatus.BAD_REQUEST, "Total amount does not match cart items");
//            }

            if (dto.getOriginalAmount() < dto.getTotalAmount()) {
                throw new CustomSystemException(HttpStatus.BAD_REQUEST, "Original amount cannot be less than total amount");
            }

            if (dto.getLoyaltyPointsUsed() != null && dto.getLoyaltyPointsUsed() > 0) {
                int availablePoints = getLoyaltyPoints(username);
                if (dto.getLoyaltyPointsUsed() > availablePoints) {
                    throw new CustomSystemException(HttpStatus.BAD_REQUEST, "Insufficient loyalty points");
                }
            }
        } catch (CustomSystemException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, "Validation failed: " + e.getMessage());
        }
    }

    private Transaction createPendingTransaction(Purchase purchase, String paymentId, BigDecimal amount, String status,
                                                 String paymentMethod, String failureReason) {
        Transaction transaction = Transaction.builder()
                .guid(UUID.randomUUID().toString())
                .purchase(purchase)
                .paymentId(paymentId)
                .amount(amount)
                .status(status)
                .paymentMethod(paymentMethod)
                .transactionDate(LocalDateTime.now())
                .CreatedOn(LocalDateTime.now())
                .failureReason(failureReason)
                .build();
        transactionDAO.save(transaction);
        log.info("✅ {} => {} => Subject :create Pending Transaction successfully",
                CHECKOUT_SERVICE_IMPL, "createPendingTransaction()");
        return transaction;
    }

    private Payment executePaypalPayment(String paymentId, String payerId) throws Exception {
        Payment payment = new Payment();
        payment.setId(paymentId);
        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);

        return payment.execute(apiContext, paymentExecution);
    }

    private void failTransaction(Transaction tx, String failureReason) {
        tx.setStatus("FAILED");
        tx.setFailureReason(failureReason);
        tx.setTransactionDate(LocalDateTime.now());
        transactionDAO.save(tx);
        log.info("✅ {} => {} => Subject :Fail transactions",
                CHECKOUT_SERVICE_IMPL, "failTransaction()");
    }

    private CheckoutDTO completeTransactionAndPurchase(Transaction tx, Purchase purchase, String payerId, CheckoutDTO checkoutDTO, String username) throws CustomSystemException {
        tx.setStatus("COMPLETED");
        tx.setPayerId(payerId);
        tx.setTransactionDate(LocalDateTime.now());
        purchase.setStatus("COMPLETED");

        User user = userUtils.getUserByUsernameOptional(username);

        if (checkoutDTO.getCouponCode() != null) {
            Coupon useCoupon = couponDao.findByCouponCode(checkoutDTO.getCouponCode())
                    .orElseThrow(() -> new CustomSystemException(HttpStatus.BAD_REQUEST, "Coupon not found"));
            useCoupon.setActive(false);
            couponDao.save(useCoupon);
        }

        updateStock(purchase);
        handleLoyaltyPoints(checkoutDTO, purchase, user);

        transactionDAO.save(tx);
        purchaseDao.save(purchase);

        int earnedPoints = calculateEarnedPoints(checkoutDTO.getFinalAmount());
        checkoutDTO.setPaymentId(tx.getPaymentId());
        checkoutDTO.setPayerId(payerId);
        checkoutDTO.setPointsEarned(earnedPoints);
        checkoutDTO.setTransactionStatus("COMPLETED");
        checkoutDTO.setTaxAmount(purchase.getTaxAmount().doubleValue());
        log.info("✅ {} => {} => Subject :complete Transaction And Purchase successfully",
                CHECKOUT_SERVICE_IMPL, "completeTransactionAndPurchase()");
        return checkoutDTO;
    }

    private void updateStock(Purchase purchase) {
        List<PurchaseItem> purchaseItems = purchaseItemDAO.findByPurchase(purchase);
        purchaseItems.forEach(item -> {
            Product product = item.getProduct();
            int newStock = product.getStockUnit() - item.getQuantity();
            product.setStockUnit(newStock);
            product.setActive(newStock > 0);
            productDao.save(product);
            log.info("✅ {} => {} => Subject :update stocks successfully",
                    CHECKOUT_SERVICE_IMPL, "updateStock()");
        });
    }

    private void handleLoyaltyPoints(CheckoutDTO checkoutDTO, Purchase purchase, User user) {
        int pointsUsed = checkoutDTO.getLoyaltyPointsUsed() != null ? checkoutDTO.getLoyaltyPointsUsed() : 0;
        if (pointsUsed > 0) {
            LoyaltyPoints spentPoints = LoyaltyPoints.builder()
                    .guid(UUID.randomUUID().toString())
                    .user(user)
                    .purchase(purchase)
                    .pointsEarned(0)
                    .pointsUsed(pointsUsed)
                    .transactionType(TransactionType.REDEEM)
                    .transactionDate(LocalDateTime.now())
                    .build();
            loyaltyPointsDAO.save(spentPoints);
            log.info("✅ {} => {} => Subject :spend loyalty points successfully",
                    CHECKOUT_SERVICE_IMPL, "handleLoyaltyPoints()");
        }

        int earnedPoints = calculateEarnedPoints(checkoutDTO.getFinalAmount());
        if (earnedPoints > 0) {
            LoyaltyPoints earned = LoyaltyPoints.builder()
                    .guid(UUID.randomUUID().toString())
                    .user(user)
                    .purchase(purchase)
                    .pointsEarned(earnedPoints)
                    .pointsUsed(0)
                    .transactionType(TransactionType.EARN)
                    .transactionDate(LocalDateTime.now())
                    .build();
            loyaltyPointsDAO.save(earned);
            log.info("✅ {} => {} => Subject :earned loyalty points successfully",
                    CHECKOUT_SERVICE_IMPL, "handleLoyaltyPoints()");
        }
    }

    private int calculateEarnedPoints(double finalAmount) {
        return (int) (finalAmount / POINTS_PER_AMOUNT);
    }

    private void restoreStock(Purchase purchase) {
        List<PurchaseItem> purchaseItems = purchaseItemDAO.findByPurchase(purchase);
        purchaseItems.forEach(item -> {
            Product product = item.getProduct();
            int restoredStock = product.getStockUnit() + item.getQuantity();
            product.setStockUnit(restoredStock);
            if (restoredStock > 0) {
                product.setActive(true);
            }
            productDao.save(product);
        });
    }

    private void cancelPendingTransactions(Purchase purchase) {
        List<Transaction> transactions = transactionDAO.findByPurchase(purchase);
        transactions.stream()
                .filter(tx -> !"COMPLETED".equals(tx.getStatus()))
                .forEach(tx -> {
                    tx.setStatus("CANCELLED");
                    tx.setFailureReason("Purchase cancelled by user");
                    tx.setTransactionDate(LocalDateTime.now());
                    transactionDAO.save(tx);
                });
    }

    private CheckoutDTO mapDto(CheckoutDTO request) {
        return CheckoutDTO.builder()
                .paymentId(request.getPaymentId())
                .couponCode(request.getCouponCode())
                .cartItems(request.getCartItems())
                .payerId(request.getPayerId())
                .originalAmount(request.getOriginalAmount())
                .totalAmount(request.getTotalAmount())
                .discountAmount(request.getDiscountAmount())
                .couponDiscount(request.getCouponDiscount())
                .taxAmount(request.getTaxAmount())
                .loyaltyPointsUsed(request.getLoyaltyPointsUsed())
                .pointsEarned(request.getPointsEarned())
                .purchaseId(request.getPurchaseId())
                .finalAmount(request.getFinalAmount())
                .approvalURl(request.getApprovalURl())
                .TransactionStatus(request.getTransactionStatus())
                .username(request.getUsername())
                .build();
    }

    private Coupon applyCoupon(CheckoutDTO request) throws CustomSystemException {
        Coupon coupon = null;
        if (request.getCouponCode() != null && !request.getCouponCode().isEmpty()) {
            coupon = couponDao.findByCouponCode(request.getCouponCode())
                    .orElseThrow(() -> new CustomSystemException(HttpStatus.BAD_REQUEST, "Coupon not found"));
            BigDecimal couponDiscount = couponServiceImpl.applyCoupon(request.getCouponCode(),
                    BigDecimal.valueOf(request.getTotalAmount()));
            request.setCouponDiscount(couponDiscount.doubleValue());
        } else {
            request.setCouponDiscount(0.0);
        }
        return coupon;
    }

    private List<Product> validateAndGetProducts(List<CartItemDTO> cartItems) throws CustomSystemException {
        List<Product> products = new ArrayList<>();
        for (CartItemDTO item : cartItems) {
            Product product = productDao.findByBarcode(item.getBarcode())
                    .orElseThrow(() -> new CustomSystemException(HttpStatus.BAD_REQUEST, ValueConstant.Product_ERROR_003));
            if (product.getStockUnit() < item.getQuantity()) {
                throw new CustomSystemException(HttpStatus.BAD_REQUEST,
                        "Insufficient stock for " + item.getBarcode() + ". Available: " + product.getStockUnit());
            }
            products.add(product);
        }
        return products;
    }

    private Purchase saveOrUpdatePurchase(CheckoutDTO request, User user, Coupon coupon, List<Product> products) throws CustomSystemException {
        Purchase purchase;
        if (request.getPurchaseId() != null) {
            purchase = purchaseDao.findById(request.getPurchaseId())
                    .orElseThrow(() -> new CustomSystemException(HttpStatus.BAD_REQUEST, "Purchase not found"));
            updatePurchase(purchase, request, coupon);
        } else {
            purchase = createNewPurchase(request, user, coupon);
            log.info("✅ {} => {} => Subject :create new purchase successfully",
                    CHECKOUT_SERVICE_IMPL, "createNewPurchase()");
        }
        purchase.setTaxAmount(BigDecimal.valueOf(request.getTaxAmount()));
        purchaseDao.save(purchase);
        savePurchaseItems(purchase, request.getCartItems(), products);
        log.info("✅ {} => {} => Subject :save purchase successfully",
                CHECKOUT_SERVICE_IMPL, "savePurchase()");
        return purchase;
    }

    private Purchase createNewPurchase(CheckoutDTO request, User user, Coupon coupon) {
        return Purchase.builder()
                .guid(UUID.randomUUID().toString())
                .user(user)
                .originalAmount(BigDecimal.valueOf(request.getOriginalAmount()))
                .totalAmount(BigDecimal.valueOf(request.getTotalAmount()))
                .discountAmount(BigDecimal.valueOf(request.getDiscountAmount()))
                .coupon(coupon)
                .couponDiscount(BigDecimal.valueOf(request.getCouponDiscount()))
                .taxAmount(BigDecimal.valueOf(request.getTaxAmount()))
                .finalAmount(BigDecimal.valueOf(request.getFinalAmount()))
                .status("PENDING")
                .createdOn(LocalDateTime.now())
                .build();
    }

    private void updatePurchase(Purchase purchase, CheckoutDTO request, Coupon coupon) {
        purchase.setOriginalAmount(BigDecimal.valueOf(request.getOriginalAmount()));
        purchase.setTotalAmount(BigDecimal.valueOf(request.getTotalAmount()));
        purchase.setDiscountAmount(BigDecimal.valueOf(request.getDiscountAmount()));
        purchase.setCouponDiscount(BigDecimal.valueOf(request.getCouponDiscount()));
        purchase.setTaxAmount(BigDecimal.valueOf(request.getTaxAmount()));
        purchase.setCoupon(coupon);
        purchase.setFinalAmount(BigDecimal.valueOf(request.getFinalAmount()));
        purchaseItemDAO.deleteByPurchase(purchase); // Delete-and-replace approach
        log.info("✅ {} => {} => Subject :save update and delete purchase successfully",
                CHECKOUT_SERVICE_IMPL, "updatePurchase()");
    }

    private void savePurchaseItems(Purchase purchase, List<CartItemDTO> cartItems, List<Product> products) {
        List<PurchaseItem> purchaseItems = new ArrayList<>();
        for (int i = 0; i < cartItems.size(); i++) {
            CartItemDTO item = cartItems.get(i);
            Product product = products.get(i);
            PurchaseItem purchaseItem = PurchaseItem.builder()
                    .guid(UUID.randomUUID().toString())
                    .purchase(purchase)
                    .product(product)
                    .quantity(item.getQuantity())
                    .price(BigDecimal.valueOf(item.getPrice()))
                    .subTotal(BigDecimal.valueOf(item.getPrice()).multiply(BigDecimal.valueOf(item.getQuantity())))
                    .build();
            purchaseItems.add(purchaseItem);
        }
        log.info("✅ {} => {} => Subject :save PurchaseItem successfully",
                CHECKOUT_SERVICE_IMPL, "savePurchaseItems()");
        purchaseItemDAO.saveAll(purchaseItems);
    }

    private Payment createPaypalPayment(Purchase purchase, double finalAmount) throws Exception {
        Amount amount = new Amount();
        amount.setCurrency(CURRENCY);
        amount.setTotal(String.format("%.2f", finalAmount));

        com.paypal.api.payments.Transaction transaction = new com.paypal.api.payments.Transaction();
        transaction.setAmount(amount);

        List<com.paypal.api.payments.Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod(PAYMENT_METHOD);

        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl("http://localhost:4200/#/purchase/cancel");
        redirectUrls.setReturnUrl("http://localhost:8080/v1/purchase/" + purchase.getPurchaseId() + "/payment");
        payment.setRedirectUrls(redirectUrls);
        log.info("✅ {} => {} => Subject : API call for PayPal successfully",
                CHECKOUT_SERVICE_IMPL, "createPaypalPayment()");
        return payment.create(apiContext);
    }

    private String extractApprovalUrl(Payment createdPayment) throws CustomSystemException {
        for (Links link : createdPayment.getLinks()) {
            if ("approval_url".equals(link.getRel())) {
                return link.getHref();
            }
        }
        throw new CustomSystemException(HttpStatus.BAD_REQUEST, "Approval URL is not found");
    }

    private double calculateSubtotal(CheckoutDTO request) throws CustomSystemException {
        // Calculate total from cart items
        double cartTotal = request.getCartItems().stream()
                .mapToDouble(item -> item.getDiscountedPrice() * item.getQuantity())
                .sum();

        // Apply coupon discount
        double couponDiscount = request.getCouponDiscount();

        // Apply loyalty points used (assuming 1 point = 1 USD)
        double loyaltyPointsUsed = request.getLoyaltyPointsUsed() != null ? request.getLoyaltyPointsUsed() : 0;

        // Validate that totalAmount from frontend matches calculated cartTotal
        if (Math.abs(cartTotal - request.getTotalAmount()) > 0.01) {
            throw new CustomSystemException(HttpStatus.BAD_REQUEST,
                    "Total amount mismatch: Frontend total amount (" + request.getTotalAmount() +
                            ") does not match backend calculated cart total (" + cartTotal + ")");
        }

        // Calculate subtotal
        return cartTotal - couponDiscount - loyaltyPointsUsed;
    }

}