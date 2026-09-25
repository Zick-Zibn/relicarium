package ru.relicarium.pledge.integration.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class LedgerClientConfig {

    @Bean
    RestClient ledgerRestClient(
            LedgerProperties ledgerProperties,
            BearerTokenForwardingInterceptor bearerTokenForwardingInterceptor) {

        return RestClient.builder()
                .baseUrl(ledgerProperties.baseUrl())
                .requestInterceptor((request, body, execution) -> {
                    bearerTokenForwardingInterceptor.apply(request.getHeaders());
                    return execution.execute(request, body);
                })
                .build();
    }
}
