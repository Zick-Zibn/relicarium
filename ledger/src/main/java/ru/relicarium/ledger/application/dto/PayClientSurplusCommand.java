package ru.relicarium.ledger.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record PayClientSurplusCommand(
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
