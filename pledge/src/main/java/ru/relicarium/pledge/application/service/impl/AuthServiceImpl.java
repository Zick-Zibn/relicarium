package ru.relicarium.pledge.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.relicarium.pledge.application.dto.request.LoginRequest;
import ru.relicarium.pledge.application.dto.response.TokenResponse;
import ru.relicarium.pledge.application.service.AuthService;
import ru.relicarium.pledge.domain.model.AppUser;
import ru.relicarium.pledge.persistence.repository.AppUserRepository;
import ru.relicarium.pledge.security.JwtProperties;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final JwtProperties jwtProperties;

    @Override
    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        AppUser user = appUserRepository.findByUsername(request.username())
                .filter(AppUser::isEnabled)
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        Instant now = Instant.now();
        Instant expiresAt = now.plus(jwtProperties.accessTokenTtl());
        String role = user.getRole().name();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(jwtProperties.issuer())
                .subject(user.getUsername())
                .issuedAt(now)
                .expiresAt(expiresAt)
                .claim("roles", List.of(role))
                .build();

        String accessToken = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        long expiresInSeconds = jwtProperties.accessTokenTtl().toSeconds();

        return new TokenResponse(accessToken, "Bearer", expiresInSeconds, user.getUsername(), List.of(role));
    }
}
