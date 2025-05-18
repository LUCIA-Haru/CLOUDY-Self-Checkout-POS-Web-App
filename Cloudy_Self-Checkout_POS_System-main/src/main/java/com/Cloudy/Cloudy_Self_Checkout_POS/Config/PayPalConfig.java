package com.Cloudy.Cloudy_Self_Checkout_POS.Config;

import com.paypal.base.rest.APIContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class PayPalConfig {
    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;

    @Value("${paypal.mode}")
    private String mode;

    @Bean
    public APIContext apiContext(){
        log.info("{} => {} => Subject : API Context for paypal",
               "PayPalConfig", "APIContext()");
        return new APIContext(clientId,clientSecret,mode);
    }
}
