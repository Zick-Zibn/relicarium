package ru.relicarium.pledge.integration.config;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class BearerTokenForwardingInterceptor {

    public void apply(HttpHeaders headers) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuthentication) {
            headers.setBearerAuth(jwtAuthentication.getToken().getTokenValue());
        }
    }
}
