package ru.relicarium.pledge.integration.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "relicarium.auction")
public record AuctionProperties(String baseUrl) {
}
