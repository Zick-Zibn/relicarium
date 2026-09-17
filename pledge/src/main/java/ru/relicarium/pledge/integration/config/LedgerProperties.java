package ru.relicarium.pledge.integration.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "relicarium.ledger")
public record LedgerProperties(String baseUrl) {
}
