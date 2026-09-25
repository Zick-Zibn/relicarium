package ru.relicarium.pledge.application.service;

import ru.relicarium.pledge.application.dto.request.LoginRequest;
import ru.relicarium.pledge.application.dto.response.TokenResponse;

public interface AuthService {

    TokenResponse login(LoginRequest request);
}
