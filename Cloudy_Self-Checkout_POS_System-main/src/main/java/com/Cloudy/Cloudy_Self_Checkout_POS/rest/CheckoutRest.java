package com.Cloudy.Cloudy_Self_Checkout_POS.rest;

import com.Cloudy.Cloudy_Self_Checkout_POS.dto.CheckoutDTO;
import com.Cloudy.Cloudy_Self_Checkout_POS.wrapper.ApiResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequestMapping(path = "v1/purchase")
public interface CheckoutRest {

    @PreAuthorize("hasAnyAuthority( 'CUSTOMER')")
    @PostMapping
    public ResponseEntity<ApiResponseWrapper<CheckoutDTO>> initiatePurchase(@RequestBody CheckoutDTO request, @AuthenticationPrincipal UserDetails userDetails);


    // Handle PayPal redirect (GET, public or minimally authenticated)
    @GetMapping("/{purchaseId}/payment")
    @Operation(
            summary = "Handle PayPal redirect",
            description = "Receives the redirect from PayPal after payment approval and returns payment details to the frontend."
    )
    public ResponseEntity<ApiResponseWrapper<Map<String, String>>> handlePaypalRedirect(
            @PathVariable Long purchaseId,
            @RequestParam("paymentId") String paymentId,
            @RequestParam(value = "PayerID", required = false) String payerId,
            @RequestParam("token") String token
    );


    @PreAuthorize("hasAnyAuthority( 'CUSTOMER')")
    @PostMapping("/{purchaseId}/payment/execute")
    @Operation(
            summary = "Execute payment for a purchase",
            description = "Completes the payment process for a specific purchase using PayPal. " +
                    "The purchase ID in the URL must match the purchase ID in the request body. " +
                    "Returns the updated checkout details on success or a failure status on error."
    )
    public ResponseEntity<ApiResponseWrapper<CheckoutDTO>> executePurchasePayment(@RequestBody CheckoutDTO checkoutDTO, @PathVariable Long purchaseId,
                                                                                  @RequestParam("paymentId") String paymentId,
                                                                                  @RequestParam("PayerID") String payerId,
                                                                                  @AuthenticationPrincipal UserDetails userDetails);

    @PreAuthorize("hasAnyAuthority( 'CUSTOMER')")
    @PostMapping("/{purchaseId}/cancel")
    public ResponseEntity<ApiResponseWrapper<String>> cancelPurchase(@PathVariable Long purchaseId);

    @PreAuthorize("hasAnyAuthority( 'CUSTOMER')")
    @GetMapping("/{username}/loyaltyPoints")
    public Integer getLoyaltyPoints(@PathVariable String username );

}
