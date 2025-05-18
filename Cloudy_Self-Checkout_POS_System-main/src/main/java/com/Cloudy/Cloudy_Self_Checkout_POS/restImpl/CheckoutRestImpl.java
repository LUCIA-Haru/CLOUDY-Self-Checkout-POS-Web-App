package com.Cloudy.Cloudy_Self_Checkout_POS.restImpl;

import com.Cloudy.Cloudy_Self_Checkout_POS.constants.ValueConstant;
import com.Cloudy.Cloudy_Self_Checkout_POS.customException.CustomSystemException;
import com.Cloudy.Cloudy_Self_Checkout_POS.dto.CheckoutDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.rest.CheckoutRest;
import com.Cloudy.Cloudy_Self_Checkout_POS.service.CheckoutService;
import com.Cloudy.Cloudy_Self_Checkout_POS.utils.CloudyUtils;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class CheckoutRestImpl implements CheckoutRest {

    @Autowired
    private CheckoutService checkoutService;

    private static  final String CheckoutRestImpl = "CheckOutRestImpl";

    @Override
    public ResponseEntity<ApiResponseWrapper<CheckoutDTO>> initiatePurchase(CheckoutDTO request, UserDetails userDetails) {
        try{
            return  checkoutService.initiatePayment(request,userDetails.getUsername());
        }catch (Exception e){
            log.error("{}=>{}=> Error:::: {}",CheckoutRestImpl,"initiatePurchase()",e.getMessage());
            return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG );
        }

    }

    @Override
    public ResponseEntity<ApiResponseWrapper<Map<String, String>>> handlePaypalRedirect(Long purchaseId, String paymentId, String payerId, String token) {
        try{
            log.info("Received GET request from PayPal: purchaseId={}, paymentId={}, payerId={}", purchaseId, paymentId, payerId);

            // Construct the redirect URL to Angular frontend
            String redirectUrl = "http://localhost:4200/#/cart/payment-return" + // Replace with your frontend URL
                    "?purchaseId=" + purchaseId +
                    "&paymentId=" + paymentId +
                    "&PayerID=" + (payerId != null ? payerId : "Not provided") +
                    "&token=" + (token != null ? token : "Not provided");

            // Redirect to frontend
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", redirectUrl)
                    .body(null);
        }catch (Exception e){
            log.error("{}=>{}=> Error:::: {}",CheckoutRestImpl,"handlePaypalRedirect()",e.getMessage());
            return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG );
        }

    }

    @Override
    public ResponseEntity<ApiResponseWrapper<CheckoutDTO>> executePurchasePayment(CheckoutDTO checkoutDTO, Long purchaseId,
                                                                                  String paymentId, String payerId, UserDetails userDetails) {
        try{
            log.info("Received POST request: purchaseId={}, paymentId={}, payerId={}", purchaseId, paymentId, payerId);
            if (!purchaseId.equals(checkoutDTO.getPurchaseId()))
                throw new CustomSystemException(HttpStatus.BAD_REQUEST,"Purchase Mismatch");
            return  checkoutService.executePayment(checkoutDTO,paymentId,payerId,userDetails.getUsername());
        }catch (Exception e){
            log.error("{}=>{}=> Error:::: {}",CheckoutRestImpl,"initiatePurchase()",e.getMessage());
            return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG );
        }
    }

    @Override
    public ResponseEntity<ApiResponseWrapper<String>> cancelPurchase(Long purchaseId) {
        try{
            return  checkoutService.cancelPurchase(purchaseId);
        }catch (Exception e){
            log.error("{}=>{}=> Error:::: {}",CheckoutRestImpl,"initiatePurchase()",e.getMessage());
            return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG );
        }
    }

    @Override
    public Integer getLoyaltyPoints(String username) {
        try{
            return  checkoutService.getLoyaltyPoints(username);
        }catch (CustomSystemException e){
            log.error("{}=>{}=> Error:::: {}",CheckoutRestImpl,"initiatePurchase()",e.getMessage());
            return CloudyUtils.getResponseEntityCustom(HttpStatus.INTERNAL_SERVER_ERROR, ValueConstant.SOMETHING_WENT_WRONG ).getStatusCodeValue();
        }
    }
}
