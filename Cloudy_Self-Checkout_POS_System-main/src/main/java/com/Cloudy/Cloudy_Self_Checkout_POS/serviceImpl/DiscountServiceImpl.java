package com.Cloudy.Cloudy_Self_Checkout_POS.serviceImpl;

import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.*;
import com.Cloudy.Cloudy_Self_Checkout_POS.constants.ValueConstant;
import com.Cloudy.Cloudy_Self_Checkout_POS.customException.CustomSystemException;
import com.Cloudy.Cloudy_Self_Checkout_POS.dao.*;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.DiscountBarcodeDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.DiscountDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.service.DiscountService;
import com.Cloudy.Cloudy_Self_Checkout_POS.utils.CloudyUtils;
import com.Cloudy.Cloudy_Self_Checkout_POS.utils.UserUtils;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DiscountServiceImpl implements DiscountService {

    private final DiscountDAO discountDAO;
    private final ProductDao productDao;
    private final CategoryDao categoryDao;

    private static String DiscountServiceImpl = "DiscountServiceImpl";

    @Autowired
    UserUtils userUtils;

    @Transactional
    @Override
    public ResponseEntity<ApiResponseWrapper<DiscountDTO>> addDiscount(DiscountDTO discountDTO, String username) {
        try{
            Staff staff;
            Product product = productDao.findById(discountDTO.getProductId()).orElseThrow(
                    ()-> new CustomSystemException(HttpStatus.BAD_REQUEST,ValueConstant.Product_ERROR_001)
            );
            Category category = categoryDao.findById(discountDTO.getCategoryId()).orElseThrow(
                    ()-> new CustomSystemException(HttpStatus.BAD_REQUEST,ValueConstant.CATEGORY_ERROR_001)
            );
            User user = userUtils.getUserByUsernameOptional(username);

            userUtils.validateStaffForUser(user,DiscountServiceImpl,"addDiscount()");
            if (discountDAO.existsByProductId(product.getProductId()))
                throw new CustomSystemException(HttpStatus.BAD_REQUEST,"Discount already exists for this product");


            Discount discount = Discount.builder()
                    .guid(UUID.randomUUID().toString())
                    .isPercentage(discountDTO.getIsPercentage())
                    .discountValue(discountDTO.getDiscountValue())
                    .startDate(discountDTO.getStartDate())
                    .endDate(discountDTO.getEndDate())
                    .product(product)
                    .category(category)
                    .createdOn(LocalDate.now())
                    .createdBy(username)
                    .build();
            Discount saveDiscount = discountDAO.save(discount);
            // Update the product's hasDiscount field
            int rowsUpdated = productDao.updateDisStatus(product.getProductId(),true);

            if (rowsUpdated == 0) {
                return CloudyUtils.getResponseEntityCustom(HttpStatus.BAD_REQUEST,ValueConstant.FAIL_ACTION);
            }
            log.info("✅ {} => {} => Subject : Adding Discount for productId {} by username ::: {} successfully",
                    DiscountServiceImpl, "addDiscount()", discountDTO.getProductId(),username);
            DiscountDTO responseBody = mapDTO(saveDiscount);

            return  CloudyUtils.getResponseEntityCustom(HttpStatus.OK,ValueConstant.ADD,responseBody);

        }catch (CustomSystemException e) {
            // Log the specific error and return the corresponding response
            log.error("{} => {} => Subject : Adding Discount for productId {} by username ::: {} => Error ::: {}",
                    DiscountServiceImpl, "addDiscount()", discountDTO.getProductId(), username, e.getMessage());
            return CloudyUtils.getResponseEntityCustom(HttpStatus.BAD_REQUEST, e.getMessage());
        }  catch (Exception e) {

            log.error("{} => {} => Subject : Adding Discount for productId {} by username ::: {} => Error ::: {}",
                    DiscountServiceImpl, "addDiscount()", discountDTO.getProductId(),username,e.getMessage());

        }
        return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResponseEntity<ApiResponseWrapper<DiscountDTO>> updateDiscount(DiscountDTO discountDTO, String username, Long productId) {
        try {
            Staff staff;
            Product product = productDao.findById(discountDTO.getProductId()).orElseThrow(
                    () -> new CustomSystemException(HttpStatus.BAD_REQUEST, ValueConstant.Product_ERROR_001)
            );
            Category category = categoryDao.findById(discountDTO.getCategoryId()).orElseThrow(
                    () -> new CustomSystemException(HttpStatus.BAD_REQUEST, ValueConstant.CATEGORY_ERROR_001)
            );
            User user = userUtils.getUserByUsernameOptional(username);

            Discount existDiscount = discountDAO.findById(discountDTO.getDiscountId()).orElseThrow(
                    () -> new CustomSystemException(HttpStatus.BAD_REQUEST, "Discount ID is not found")
            );

            userUtils.validateStaffForUser(user,DiscountServiceImpl,"updateDiscount()");

            Discount updateDiscount = updateDiscountFields(existDiscount,discountDTO,username,product,category);
            Discount saveDiscount = discountDAO.save(updateDiscount);
            log.info("✅ {} => {} => Subject : Updating Discount for productId {} by username ::: {} successfully",
                    DiscountServiceImpl, "updateDiscount()", discountDTO.getProductId(),username);
            DiscountDTO responseBody = mapDTO(saveDiscount);
            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, ValueConstant.UPDATE,responseBody );

        } catch (CustomSystemException e) {
            // Log the specific error and return the corresponding response
            log.error("{} => {} => Subject : updating Discount for productId {} by username ::: {} => Error ::: {}",
                    DiscountServiceImpl, "updateDiscount()", discountDTO.getProductId(), username, e.getMessage());
            return CloudyUtils.getResponseEntityCustom(HttpStatus.BAD_REQUEST, e.getMessage());
        }catch (Exception e) {
            log.error("{} => {} => Subject : Updating Discount for productId {} by username ::: {} => Error ::: {}",
                    DiscountServiceImpl, "updateDiscount()", discountDTO.getProductId(),username,e.getMessage());
            return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }

    @Transactional
    @Override
    public ResponseEntity<ApiResponseWrapper<String>> deleteDiscount( String username, Long discountId) {
        try {
            Discount existDiscount = discountDAO.findById(discountId).orElseThrow(
                    () -> new CustomSystemException(HttpStatus.BAD_REQUEST, "Discount ID is not found")
            );
            productDao.findById(existDiscount.getProduct().getProductId()).orElseThrow(
                    () -> new CustomSystemException(HttpStatus.BAD_REQUEST, ValueConstant.Product_ERROR_001)
            );
            User user = userUtils.getUserByUsernameOptional(username);

            userUtils.validateStaffForUser(user, DiscountServiceImpl, "deleteDiscount()");
            // Update the product's hasDiscount field
            int rowsUpdated = productDao.updateDisStatus(existDiscount.getProduct().getProductId(),false);

            if (rowsUpdated == 0) {
                return CloudyUtils.getResponseEntityCustom(HttpStatus.BAD_REQUEST,ValueConstant.FAIL_ACTION);
            }
            log.info("✅ {} => {} => Subject :Deleting Discount for productId {} by username ::: {} successfully",
                    DiscountServiceImpl, "deleteDiscount()", existDiscount.getProduct().getProductId(),username);
            discountDAO.delete(existDiscount);

            return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, ValueConstant.DELETE);

        }catch (CustomSystemException e) {
            // Log the specific error and return the corresponding response
            log.error("{} => {} => Subject : Deleting Discount for discountId {} by username ::: {} => Error ::: {}",
                    DiscountServiceImpl, "deleteDiscount()", discountId, username, e.getMessage());
            return CloudyUtils.getResponseEntityCustom(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<List<DiscountBarcodeDTO>>> getActiveDiscounts(List<String> barcodes) throws CustomSystemException {
        log.info("{} => {} => Subject : getActiveDiscounts  barcodes::: {}",
                DiscountServiceImpl, "getActiveDiscounts", barcodes);
        try{
              LocalDate today = LocalDate.now();
              List<DiscountBarcodeDTO> discountBarcodeDTO = discountDAO.findActiveDiscountsByBarcodes(barcodes,today);
              if (discountBarcodeDTO.isEmpty()) throw new CustomSystemException(HttpStatus.BAD_REQUEST,"No Active Discounts are Found");

            List<DiscountBarcodeDTO> responses = new ArrayList<>();
              for ( DiscountBarcodeDTO item : discountBarcodeDTO){
              DiscountBarcodeDTO response = DiscountBarcodeDTO.builder()
                      .barcode(item.getBarcode())
                      .discount(item.getDiscount())
                      .build();
              responses.add(response);

              }
            log.info("✅ {} => {} => Subject : getActiveDiscounts  barcodes::: {} successfully",
                    DiscountServiceImpl, "getActiveDiscounts", barcodes);
              return CloudyUtils.getResponseEntityCustom(HttpStatus.OK, ValueConstant.RETRIEVE,responses);

        }catch (CustomSystemException e) {
            // Log the specific error and return the corresponding response
            log.error("{} => {} => Subject : Getting active discounts  => Error ::: {}",
                    DiscountServiceImpl, "getActiveDiscount()", e.getMessage());
            throw (e);
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<PaginatedResponse<DiscountDTO>>> getAllDiscounts(String username, String filterValue, int page, int size) throws CustomSystemException {
        log.info("{} => getAllDiscounts() => Subject: retrieving all discounts ||| username: {}", DiscountServiceImpl, username);

        try {
            // Validate user
            User user = userUtils.getUserByUsernameOptional(username);
            userUtils.validateStaffForUser(user, DiscountServiceImpl, "getAllDiscounts()");

            Page<Discount> discountPage;
            Pageable pageable = PageRequest.of(page, size);
            LocalDate today = LocalDate.now();

            // Fetch discounts based on filterValue
//            if ("false".equalsIgnoreCase(filterValue)) {
////                discountPage = discountDAO.findByInactive(today, pageable);
//            } else {
//            }
                discountPage = discountDAO.findAll(pageable);

            // Map entities to DTOs
            List<DiscountDTO> discountDTOs = discountPage.getContent().stream()
                    .map(this::mapDTO)
                    .collect(Collectors.toList());

            // Build paginated response
            PaginatedResponse<DiscountDTO> paginatedResponse = new PaginatedResponse<>(
                    discountDTOs,
                    discountPage.getTotalElements(),
                    discountPage.getTotalPages(),
                    discountPage.getNumber(),
                    discountPage.getSize()
            );

            log.info("✅ {} => getAllDiscounts() => Subject: retrieving all discounts || username: {}", DiscountServiceImpl, username);
            return CloudyUtils.getPaginatedResponseEntityCustom(HttpStatus.OK, ValueConstant.RETRIEVE, paginatedResponse);

        } catch (CustomSystemException e) {
            log.error("{} => getAllDiscounts() => Subject: retrieving all discounts => Custom Error: {}", DiscountServiceImpl, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("{} => getAllDiscounts() => Subject: retrieving all discounts => Unexpected Error: ", DiscountServiceImpl, e);
            throw new CustomSystemException(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG);
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////MapToDTO
    private DiscountDTO mapDTO (Discount discount){
        DiscountDTO dto = new DiscountDTO();
        dto.setDiscountId(discount.getDiscountId());
        dto.setGuid(discount.getGuid());
        dto.setProductId(discount.getProduct().getProductId());
        dto.setCategoryId(discount.getCategory().getCategoryId());
        dto.setIsPercentage(discount.isPercentage());
        dto.setDiscountValue(discount.getDiscountValue());
        dto.setStartDate(discount.getStartDate());
        dto.setEndDate(discount.getEndDate());
        dto.setProductName(discount.getProduct().getProductName());
        dto.setCategoryName(discount.getCategory().getCategoryName());
        return dto;
    }
//    update
private Discount updateDiscountFields(Discount exist, DiscountDTO dto, String username,Product product,Category category) {
    return Discount.builder()
            .discountId(exist.getDiscountId())
            .guid(exist.getGuid())
            .isPercentage(dto.getIsPercentage() != null
                    ? dto.getIsPercentage()
                    : exist.isPercentage())
            .discountValue(dto.getDiscountValue() != null
                    ? dto.getDiscountValue()
                    : exist.getDiscountValue())
            .startDate(dto.getStartDate() != null ? dto.getStartDate() : exist.getStartDate())
            .endDate(dto.getEndDate() != null ? dto.getEndDate() : exist.getEndDate())
            .category(dto.getCategoryId() != null ? category : exist.getCategory())
            .product(dto.getProductId() != null ? product : exist.getProduct())
            .createdOn(exist.getCreatedOn())
            .createdBy(exist.getCreatedBy())
            .updatedOn(LocalDate.now())
            .updatedBy(username)
            .build();
}
    private boolean isDiscountActive(Discount discount, LocalDate today) {
        return !today.isBefore(discount.getStartDate()) &&
                (discount.getEndDate() == null || !today.isAfter(discount.getEndDate()));
    }

}
