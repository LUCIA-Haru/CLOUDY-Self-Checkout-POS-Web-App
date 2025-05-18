package com.Cloudy.Cloudy_Self_Checkout_POS.serviceImpl;

import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.Product;
import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.Supplier;
import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.SupplierTransaction;
import com.Cloudy.Cloudy_Self_Checkout_POS.constants.ValueConstant;
import com.Cloudy.Cloudy_Self_Checkout_POS.customException.CustomSystemException;
import com.Cloudy.Cloudy_Self_Checkout_POS.dao.SupplierDao;
import com.Cloudy.Cloudy_Self_Checkout_POS.dao.SupplierTransactionDAO;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.ProductDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.SupplierDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.SupplierTransactionDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.service.SupplierService;
import com.Cloudy.Cloudy_Self_Checkout_POS.utils.CloudyUtils;
import com.Cloudy.Cloudy_Self_Checkout_POS.utils.UserUtils;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.PaginatedResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class SupplierServiceImpl implements SupplierService {

    private final UserUtils userUtils;
    private final SupplierDao supplierDao;
    private final SupplierTransactionDAO supplierTransactionDAO;

    private static final String SERVICE_NAME = "SupplierServiceImpl";

    @Override
    public ResponseEntity<ApiResponseWrapper<Supplier>> addSupplier(String username, SupplierDTO dto) {
        log.info("{} => addSupplier() => Subject: adding supplier ||| username: {}", SERVICE_NAME, username);

        try {
            // Validate user
            userUtils.getUserByUserName(username);

            // Create new supplier entity
            Supplier supplier = Supplier.builder()
                    .guid(UUID.randomUUID().toString())
                    .name(dto.getName())
                    .contactEmail(dto.getContactEmail())
                    .contactPhone(dto.getContactPhone())
                    .isMainSupplier(dto.getIsMainSupplier())
                    .contractDurationInMonths(dto.getContractDurationInMonths())
                    .isActive(dto.getIsActive())
                    .createdOn(LocalDate.now())
                    .createdBy(username)
                    .build();

            // Save supplier to database
            Supplier savedSupplier = supplierDao.save(supplier);

            log.info("✅ {} => addSupplier() => Subject: adding supplier || username: {}", SERVICE_NAME, username);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, ValueConstant.ADD, savedSupplier);

        } catch (Exception e) {
            log.error("{} => addSupplier() => Subject: adding supplier => Unexpected Error: ", SERVICE_NAME, e);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<Supplier>> updateSupplier(String username, SupplierDTO dto) {
        log.info("{} => updateSupplier() => Subject: updating supplier ||| username: {}, supplierId: {}", SERVICE_NAME, username, dto.getId());

        try {
            // Validate user
            userUtils.getUserByUserName(username);

            // Fetch existing supplier by ID
            Supplier existingSupplier = supplierDao.findById(dto.getId())
                    .orElseThrow(() -> new CustomSystemException(HttpStatus.BAD_REQUEST, ValueConstant.ID_NOT_FOUND));

            // Update fields
            existingSupplier.setName(dto.getName() != null ? dto.getName() : existingSupplier.getName());
            existingSupplier.setContactEmail(dto.getContactEmail() != null ? dto.getContactEmail() : existingSupplier.getContactEmail());
            existingSupplier.setContactPhone(dto.getContactPhone() != null ? dto.getContactPhone() : existingSupplier.getContactPhone());
            existingSupplier.setIsMainSupplier(dto.getIsMainSupplier() != null ? dto.getIsMainSupplier() : existingSupplier.getIsMainSupplier());
            existingSupplier.setContractDurationInMonths(dto.getContractDurationInMonths() != null ? dto.getContractDurationInMonths() : existingSupplier.getContractDurationInMonths());
            existingSupplier.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : existingSupplier.getIsActive());
            existingSupplier.setUpdatedOn(LocalDate.now());
            existingSupplier.setUpdatedBy(username);

            // Save updated supplier
            Supplier updatedSupplier = supplierDao.save(existingSupplier);

            log.info("✅ {} => updateSupplier() => Subject: updating supplier || username: {}, supplierId: {}", SERVICE_NAME, username, dto.getId());
            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, ValueConstant.UPDATE, updatedSupplier);

        } catch (CustomSystemException e) {
            log.error("{} => updateSupplier() => Subject: updating supplier => Custom Error: {}", SERVICE_NAME, e.getMessage());
            return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);

        } catch (Exception e) {
            log.error("{} => updateSupplier() => Subject: updating supplier => Unexpected Error: ", SERVICE_NAME, e);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<String>> deleteSupplier(String username, Long supplierId) {
        log.info("{} => deleteSupplier() => Subject: deleting supplier id {} ||| username: {}", SERVICE_NAME, supplierId, username);

        try {
            // Validate user
            userUtils.getUserByUserName(username);

            // Fetch existing supplier by ID
            Supplier supplier = supplierDao.findById(supplierId)
                    .orElseThrow(() -> new CustomSystemException(HttpStatus.BAD_REQUEST, ValueConstant.ID_NOT_FOUND));

            // Delete supplier
            supplierDao.deleteById(supplier.getId());

            log.info("✅ {} => deleteSupplier() => Subject: deleting supplier || username: {}, supplierId: {}", SERVICE_NAME, username, supplierId);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, ValueConstant.DELETE, ValueConstant.ACTION_SUCCESS);

        } catch (CustomSystemException e) {
            log.error("{} => deleteSupplier() => Subject: deleting supplier => Custom Error: {}", SERVICE_NAME, e.getMessage());
            return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);

        } catch (Exception e) {
            log.error("{} => deleteSupplier() => Subject: deleting supplier => Unexpected Error: ", SERVICE_NAME, e);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<Supplier>> getSupplierById(String username, Long supplierId) {
        log.info("{} => getSupplierById() => Subject: getting supplier id {} ||| username: {}", SERVICE_NAME, supplierId, username);

        try {
            // Validate user
            userUtils.getUserByUserName(username);

            // Fetch supplier by ID
            Supplier supplier = supplierDao.findById(supplierId)
                    .orElseThrow(() -> new CustomSystemException(HttpStatus.BAD_REQUEST, ValueConstant.ID_NOT_FOUND));

            log.info("✅ {} => getSupplierById() => Subject: getting supplier || username: {}, supplierId: {}", SERVICE_NAME, username, supplierId);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, ValueConstant.RETRIEVE, supplier);

        } catch (CustomSystemException e) {
            log.error("{} => getSupplierById() => Subject: getting supplier => Custom Error: {}", SERVICE_NAME, e.getMessage());
            return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);

        } catch (Exception e) {
            log.error("{} => getSupplierById() => Subject: getting supplier => Unexpected Error: ", SERVICE_NAME, e);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<PaginatedResponse<Supplier>>> getAllSuppliers(String username, String filterValue, int page, int size) {
        log.info("{} => getAllSuppliers() => Subject: get all suppliers ||| username: {}", SERVICE_NAME, username);

        try {
            // Validate user
            userUtils.getUserByUserName(username);

            // Validate pagination parameters
            if (page < 0 || size <= 0) {
                throw new IllegalArgumentException("Invalid pagination parameters");
            }

            // Create pageable object
            Pageable pageable = PageRequest.of(page, size);

            // Fetch suppliers based on filterValue
            Page<Supplier> suppliers;
            if ("inactive".equalsIgnoreCase(filterValue)) {
                suppliers = supplierDao.findByInactive(pageable);
            } else {
                suppliers = supplierDao.findAll(pageable);
            }

            // Build paginated response
            PaginatedResponse<Supplier> paginatedResponse = new PaginatedResponse<>(suppliers);

            log.info("✅ {} => getAllSuppliers() => Subject: get all suppliers || username: {}", SERVICE_NAME, username);
            return CloudyUtils.getPaginatedResponseEntityCustom(HttpStatus.OK, ValueConstant.RETRIEVE, paginatedResponse);

        } catch (IllegalArgumentException e) {
            log.error("{} => getAllSuppliers() => Subject: get all suppliers => Invalid Input: {}", SERVICE_NAME, e.getMessage());
            return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);

        } catch (Exception e) {
            log.error("{} => getAllSuppliers() => Subject: get all suppliers => Unexpected Error: ", SERVICE_NAME, e);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<List<SupplierTransactionDTO>>> getSupplierTransaction(String username) {
        log.info("{} =>getSupplierTransaction()=> username ::: {}",
                SERVICE_NAME,username);
        try{
            userUtils.getUserByUsernameOptional(username);
            List<SupplierTransaction> transactions = supplierTransactionDAO.findAllByOrderByTransactionDateDesc();
            log.info("✅Fetching supplier transaction successfully");

            List<SupplierTransactionDTO> dto = transactions.stream().map(
                    transaction -> SupplierTransactionDTO.builder()
                            .guid(transaction.getGuid())
                            .supplierId(transaction.getSupplier().getId())
                            .transactionId(transaction.getTransactionId())
                            .transactionDate(transaction.getTransactionDate())
                            .quantity((int) transaction.getQuantity())
                            .brandId(transaction.getBrand().getId())
                            .categoryId(transaction.getCategory().getCategoryId())
                            .createdBy(transaction.getCreatedBy())
                            .product(transaction.getProducts().stream()
                                    .map(this::mapToProductDTO)
                                    .collect(Collectors.toList()))
                            .build()
            ).collect(Collectors.toList());


            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK,ValueConstant.RETRIEVE,dto);

        }catch (Exception e) {
            log.error("{} => getAllSuppliers() => Subject: get all suppliers => Unexpected Error: ", SERVICE_NAME, e);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }
    private ProductDTO mapToProductDTO(Product product) {
        ProductDTO dto = new ProductDTO();
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