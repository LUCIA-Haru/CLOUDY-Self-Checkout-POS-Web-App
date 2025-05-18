package com.Cloudy.Cloudy_Self_Checkout_POS.serviceImpl;

import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.*;
import com.Cloudy.Cloudy_Self_Checkout_POS.constants.ValueConstant;
import com.Cloudy.Cloudy_Self_Checkout_POS.customException.CustomSystemException;
import com.Cloudy.Cloudy_Self_Checkout_POS.customException.GlobalExceptionHandler;
import com.Cloudy.Cloudy_Self_Checkout_POS.dao.*;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.*;
import com.Cloudy.Cloudy_Self_Checkout_POS.service.CustomerService;
import com.Cloudy.Cloudy_Self_Checkout_POS.utils.CloudyUtils;
import com.Cloudy.Cloudy_Self_Checkout_POS.utils.PdfUtils;
import com.Cloudy.Cloudy_Self_Checkout_POS.utils.UserUtils;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.CustomerWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService {



   private final CustomerDao customerDao;

    @Autowired
    UserDao userDao;


    @Autowired
    UserUtils userUtils;

    @Autowired
    GlobalExceptionHandler globalExceptionHandler;

    @Autowired
    PdfUtils pdfUtils;

    @Autowired
    PurchaseDao purchaseDao;

    @Autowired
    PurchaseItemDAO purchaseItemDAO;


    @Autowired
    TransactionDAO transactionDAO;

    public CustomerServiceImpl(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<CustomerWrapper>> getCustomer(UserDetails userDetails) {
        Customer customer;
        try {
            User user = userUtils.getUserByUsernameOptional(userDetails.getUsername());

             customer = customerDao.findByUserId(user.getUserId());
            CustomerWrapper newResponse = new CustomerWrapper(customer);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK,ValueConstant.ACTION_SUCCESS,newResponse);

        }catch (CustomSystemException ex){
            ex.getMessage();
            ex.printStackTrace();
            return globalExceptionHandler.handleCustomSystemException(ex);
        }catch (Exception e){
            e.printStackTrace();
        }
        return CloudyUtils.getResponseEntityCustom( HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<CustomerWrapper>> updateCustomer(UserDetails userDetails, CustomerRequestBody request) {
        try {
            Customer customer;
           User user = userUtils.getUserByUsernameOptional(userDetails.getUsername());

                 customer = customerDao.findByUserId(user.getUserId());
                if (customer == null){
                    log.warn("Customer not found for userId: {}", user.getUserId());
                    return CloudyUtils.getResponseEntityCustom(HttpStatus.BAD_REQUEST, ValueConstant.CUSTOMER_NOT_FOUND);
                }

                updateCustomerFields(customer, request);
                customer.setUpdatedOn(LocalDateTime.now());
                Customer updatedCustomer = customerDao.save(customer);

                CustomerWrapper newResponse = new CustomerWrapper(updatedCustomer);
                return CloudyUtils.getResponseEntityCustom(HttpStatus.OK,ValueConstant.ACTION_SUCCESS,newResponse);

        } catch (CustomSystemException ex) {
            log.error("CustomSystemException occurred: {}", ex.getMessage(), ex);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        } catch (Exception ex) {
            log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }

    }

    @Override
    public ResponseEntity<ApiResponseWrapper<String>> uploadProfileImg(UserDetails userDetails, MultipartFile file) {
        return null;
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<Object>> generateVoucherPdf(String username, CheckoutDTO checkout) {
        try{
            String pdfUrl = pdfUtils.generateVoucherPDF(checkout);

            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK,ValueConstant.ACTION_SUCCESS,pdfUrl);

        }catch (Exception e){
            log.error("Unexpected error occurred: {}", e.getMessage(), e);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<Resource> servePdf(String username, String filename) throws CustomSystemException {
        try {
            log.info("Serving PDF for user: {}, filename: {}", username, filename);
            userUtils.getUserByUsernameOptional(username);

            Path filePath = pdfUtils.getPdf(username, filename);
            Resource file = new org.springframework.core.io.PathResource(filePath);

            if (!file.exists() || !file.isReadable()) {
                throw new CustomSystemException(HttpStatus.BAD_REQUEST.toString(), "File not found or not readable: " + filename);
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                    .body(file);

        } catch (CustomSystemException e) {
            log.error("Custom error occurred: {}", e.getMessage(), e);
            throw e; // Let Spring handle the exception and return appropriate status
        } catch (Exception e) {
            log.error("Unexpected error occurred: {}", e.getMessage(), e);
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), ValueConstant.SOMETHING_WENT_WRONG);
        }
    }


    @Override
    public ResponseEntity<ApiResponseWrapper<Object>> getAllPdf(String username) {

        try{
            log.info("Fetching all PDFs for user: {}", username);
            userUtils.getUserByUsernameOptional(username);
            List<String> pdfUrls = pdfUtils.getAllPdfs(username);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, ValueConstant.ACTION_SUCCESS, pdfUrls);

        }catch (Exception e){
            log.error("Unexpected error occurred: {}", e.getMessage(), e);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<List<PurchaseDTO>>> getPurchaseHistory(String username) throws CustomSystemException {
        try {
            log.info("Fetching purchase history for user: {}", username);

            // Fetch the user by username
            User user = userDao.findByUsername(username);

            // Fetch purchases for the user
            List<Purchase> purchases = purchaseDao.findByUserOrderByPurchaseIdDesc(user);
            if (purchases.isEmpty()) {
                log.info("No purchases found for user: {}", username);
                throw new CustomSystemException(HttpStatus.BAD_REQUEST.toString(),"No purchases found for user: {}", username);
            }

            // Fetch all PDFs associated with the user
            List<String> pdfUrls = pdfUtils.getAllPdfs(username);

            // Map purchases to DTOs/
            List<PurchaseDTO> dtoList = purchases.stream().map(
                    purchase -> {
                        PurchaseDTO dto = new PurchaseDTO();
                        dto.setPurchaseId(purchase.getPurchaseId());
                        dto.setGuid(purchase.getGuid());
                        dto.setUserId(purchase.getUser().getUserId());
                        dto.setUsername(purchase.getUser().getUsername());
                        dto.setTaxAmount(purchase.getTaxAmount());
                        dto.setOriginalAmount(purchase.getOriginalAmount());
                        dto.setTotalAmount(purchase.getTotalAmount());
                        dto.setDiscountAmount(purchase.getDiscountAmount());
                        dto.setCouponDiscount(purchase.getCouponDiscount());
                        dto.setFinalAmount(purchase.getFinalAmount());
                        dto.setStatus(purchase.getStatus());
                        dto.setCouponId(purchase.getCoupon() != null ? purchase.getCoupon().getCouponId() : null);
                        dto.setCreatedOn(purchase.getCreatedOn());
                        dto.setCurrency(purchase.getCurrency());

                        // Fetch purchase items
                        List<PurchaseItem> items = purchaseItemDAO.findByPurchase(purchase);
                        List<PurchaseItemDTO> itemDTOs = items.stream().map(item -> {
                            PurchaseItemDTO itemDTO = new PurchaseItemDTO();
                            itemDTO.setPurchaseItemId(item.getPurchaseItemId());
                            itemDTO.setGuid(item.getGuid());
                            itemDTO.setPurchaseId(item.getPurchase().getPurchaseId());
                            itemDTO.setProductId(item.getProduct().getProductId());
                            itemDTO.setQuantity(item.getQuantity());
                            itemDTO.setPrice(item.getPrice());
                            itemDTO.setSubTotal(item.getSubTotal());
                            return itemDTO;
                        }).collect(Collectors.toList());
                        dto.setItems(itemDTOs);

                        // Fetch the latest transaction
                        List<Transaction> transactions = transactionDAO.findByPurchaseOrderByTransactionDateDesc(purchase);
                        TransactionDTO transactionDTO = null;
                        if (!transactions.isEmpty()) {
                            Transaction transaction = transactions.get(0); // Latest transaction
                            transactionDTO = new TransactionDTO();
                            transactionDTO.setTransactionId(transaction.getTransactionId());
                            transactionDTO.setGuid(transaction.getGuid());
                            transactionDTO.setPurchaseId(transaction.getPurchase().getPurchaseId());
                            transactionDTO.setPaymentId(transaction.getPaymentId());
                            transactionDTO.setAmount(transaction.getAmount());
                            transactionDTO.setStatus(transaction.getStatus());
                            transactionDTO.setPaymentMethod(transaction.getPaymentMethod());
                            transactionDTO.setTransactionDate(transaction.getTransactionDate());
                            transactionDTO.setPayerId(transaction.getPayerId());
                            transactionDTO.setCreatedOn(transaction.getCreatedOn());
                            transactionDTO.setFailureReason(transaction.getFailureReason());
                        }
                        dto.setTransaction(transactionDTO);

                        // Find the matching PDF for this purchase
                        String pdfFilename = String.format("voucher-%s-%s.pdf", username, purchase.getPurchaseId());
                        log.info("PDF URLs for user {}: {}", username, pdfUrls);
                        String pdfUrl = pdfUrls.stream()
                                .filter(url -> url.endsWith(pdfFilename))
                                .findFirst()
                                .orElse(null);
                        dto.setVoucherPdfUrl(pdfUrl);

                        return dto;
                    }).collect(Collectors.toList());


            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, ValueConstant.ACTION_SUCCESS,dtoList );


        } catch (CustomSystemException e) {
            log.error("Custom error occurred: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred: {}", e.getMessage(), e);
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Failed to fetch purchase history");
        }
    }


    private String removeLeadingZero(String phoneNo) {
        if (phoneNo.startsWith("0")) {
            return phoneNo.substring(1); // Remove the first character (leading zero)
        }
        return phoneNo; // Return as-is if no leading zero
    }

//    private void updateCustomerFields(Customer customer, CustomerRequestBody request) throws CustomSystemException {
//        Field[] requestFields = request.getClass().getDeclaredFields();
//        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//
//        for (Field requestField : requestFields) {
//            try {
//                requestField.setAccessible(true);
//                Object newValue = requestField.get(request);
//
//                // Skip updating if the value is null or an empty string
//                if (newValue == null || (newValue instanceof String && ((String) newValue).trim().isEmpty())) {
//                    continue;
//                }
//
//                Field customerField = Customer.class.getDeclaredField(requestField.getName());
//                customerField.setAccessible(true);
//
//                // Special handling for date fields
//                if ("dob".equals(requestField.getName()) && newValue instanceof String) {
//                    try {
//                        LocalDate dob = LocalDate.parse((String) newValue, dateFormatter);
//                        customerField.set(customer, dob);
//                    } catch (DateTimeParseException e) {
//                        throw new CustomSystemException("Invalid date format for DOB. Expected format: yyyy-MM-dd");
//                    }
//                } else if ("phoneNo".equals(requestField.getName()) && newValue instanceof String) {
//                    String cleanedPhoneNo = removeLeadingZero((String) newValue);
//                    customerField.set(customer, cleanedPhoneNo);
//                } else {
//                    customerField.set(customer, newValue);
//                }
//            } catch (NoSuchFieldException | IllegalAccessException | CustomSystemException e) {
//                log.warn("Failed to update field: {}", requestField.getName(), e);
//            }
//        }
//    }










    private void updateCustomerFields(Customer customer, CustomerRequestBody request) throws CustomSystemException {
        // Standard date format for parsing
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        dateFormatter.setLenient(false); // Strict parsing to avoid invalid dates like "2023-02-30"

        if (request.getFirstName() != null && !request.getFirstName().isEmpty()) {
            customer.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null && !request.getLastName().isEmpty()) {
            customer.setLastName(request.getLastName());
        }

        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            customer.setEmail(request.getEmail());
        }

        if (request.getAddress() != null && !request.getAddress().isEmpty()) {
            customer.setAddress(request.getAddress());
        }

        if (request.getDob() != null) {
            customer.setDob(request.getDob());
        }

        if (request.getPhoneNo() != null && !request.getPhoneNo().isEmpty()) {
            String cleanedPhoneNo = removeLeadingZero(request.getPhoneNo());
            customer.setPhoneNo(cleanedPhoneNo);
        }

    }

}
