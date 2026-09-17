package ru.relicarium.ledger.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record PayInterestRequest(
        @NotNull
        UUID pledgeId,
        @Positive
        BigDecimal amount,
        @NotBlank
        String operationId,
        OffsetDateTime paidAt
) {
}
