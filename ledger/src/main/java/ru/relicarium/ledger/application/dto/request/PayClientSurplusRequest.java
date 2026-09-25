package ru.relicarium.ledger.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record PayClientSurplusRequest(
        @NotNull
        UUID pledgeId,
        @NotNull
        String operationId,
        @NotNull
        @Positive
        BigDecimal amount,
        OffsetDateTime paidAt
) {
}
