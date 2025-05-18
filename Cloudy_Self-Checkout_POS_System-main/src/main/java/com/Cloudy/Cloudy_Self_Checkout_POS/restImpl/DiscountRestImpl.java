package com.Cloudy.Cloudy_Self_Checkout_POS.restImpl;

import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.Discount;
import com.Cloudy.Cloudy_Self_Checkout_POS.constants.ValueConstant;
import com.Cloudy.Cloudy_Self_Checkout_POS.customException.CustomSystemException;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.DiscountBarcodeDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.DiscountDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.rest.DiscountRest;
import com.Cloudy.Cloudy_Self_Checkout_POS.service.DiscountService;
import com.Cloudy.Cloudy_Self_Checkout_POS.utils.CloudyUtils;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class DiscountRestImpl implements DiscountRest {
    private final DiscountService discountService;

    private static final String DiscountRestImpl = "DiscountRestImpl";

    @Override
    public ResponseEntity<ApiResponseWrapper<DiscountDTO>> addDiscount(DiscountDTO discountDTO, UserDetails userDetails) {
        try{
            return discountService.addDiscount(discountDTO,userDetails.getUsername());
        }catch (Exception e){
            e.printStackTrace();
        }
        return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG );
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<DiscountDTO>> updateDiscount(DiscountDTO discountDTO, UserDetails userDetails, Long productId) {
        try{
            return discountService.updateDiscount(discountDTO,userDetails.getUsername(),productId);
        }catch (Exception e){
            e.printStackTrace();
        }
        return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG );
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<String>> deleteDiscount( UserDetails userDetails, Long discountId) {
        try{
            return discountService.deleteDiscount(userDetails.getUsername(),discountId);
        }catch (Exception e){
            log.error("{} => {} => Subject : Deleting Discount for discountId {} by username ::: {} => Error ::: {}",
                    DiscountRestImpl, "deleteDiscount()", discountId, userDetails.getUsername(), e.getMessage());
        }
        return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG );
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<List<DiscountBarcodeDTO>>> getActiveDiscounts(List<String> barcodes) throws CustomSystemException {
        try{
            return discountService.getActiveDiscounts(barcodes);
        }catch (Exception e){
            log.error("{} => {} => Subject : Getting active discounts  => Error ::: {}",
                    DiscountRestImpl, "getActiveDiscount()", e.getMessage());
            throw e;
        }

    }
    @Override
    public ResponseEntity<ApiResponseWrapper<PaginatedResponse<DiscountDTO>>> getAllDiscounts(
            UserDetails userDetails, String filterValue, int page, int size) throws CustomSystemException {
        try {
            return discountService.getAllDiscounts(userDetails.getUsername(), filterValue, page, size);
        } catch (Exception e) {
            log.error("{} => {} => Subject : Getting all discounts for username ::: {} => Error ::: {}",
                    DiscountRestImpl, "getAllDiscounts()", userDetails.getUsername(), e.getMessage());
            throw e;
        }
    }
}
