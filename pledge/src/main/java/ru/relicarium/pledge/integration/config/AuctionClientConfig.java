package ru.relicarium.pledge.integration.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class AuctionClientConfig {

    @Bean
    public RestClient auctionRestClient(AuctionProperties properties) {
        return RestClient.builder().baseUrl(properties.baseUrl()).build();
    }
}
