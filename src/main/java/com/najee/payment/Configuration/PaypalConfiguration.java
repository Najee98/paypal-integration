package com.najee.payment.Configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaypalConfiguration {

    @Value("${paypal.client-id}")
    private String clientId;
    @Value("${paypal.client-secret}")
    private String clientSecret;
    @Value("${paypal.mode}")
    private String mode;

    //ApiContext is from the package com.paypal.base.rest, which is provided in the paypal-core dependency
    @Bean
    public ApiContext apiContext(){
        return new ApiContext(clientId, clientSecret, mode);
    }
}
