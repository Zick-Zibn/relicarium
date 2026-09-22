package ru.relicarium.auction.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateLotRequest(
        @NotNull
        UUID pledgeId,
        @NotBlank
        String operationId
) {
}
