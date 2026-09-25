package ru.relicarium.pledge.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "relicarium.security.jwt")
public record JwtProperties(
        String secret,
        String issuer,
        Duration accessTokenTtl
) {
}
