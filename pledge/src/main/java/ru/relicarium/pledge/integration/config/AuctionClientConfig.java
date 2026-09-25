package ru.relicarium.pledge.integration.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class AuctionClientConfig {

    @Bean
    public RestClient auctionRestClient(
            AuctionProperties properties,
            BearerTokenForwardingInterceptor bearerTokenForwardingInterceptor) {
        return RestClient.builder()
                .baseUrl(properties.baseUrl())
                .requestInterceptor((request, body, execution) -> {
                    bearerTokenForwardingInterceptor.apply(request.getHeaders());
                    return execution.execute(request, body);
                })
                .build();
    }
}
