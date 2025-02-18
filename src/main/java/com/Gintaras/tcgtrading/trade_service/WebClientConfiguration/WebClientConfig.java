package com.Gintaras.tcgtrading.trade_service.WebClientConfiguration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@PropertySource("classpath:application-${spring.profiles.active}.properties")
public class WebClientConfig {

    @Value("${user.service.url}")
    private String userServiceUrl;

    @Value("${card.service.url}")
    private String cardServiceUrl;


    @Bean("user")
    public WebClient userService(WebClient.Builder builder) {
        return builder.baseUrl(userServiceUrl).build();
    }

    @Bean("usercard")
    public WebClient cardService(WebClient.Builder builder) {
        return builder.baseUrl(cardServiceUrl).build();
    }

}