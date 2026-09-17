package ru.relicarium.ledger.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record DisburseLoanRequest(
        @NotNull
        UUID pledgeId,
        @NotNull
        UUID clientId,
        @Positive
        BigDecimal principal,
        @PositiveOrZero
        BigDecimal interestRate,
        @NotNull
        OffsetDateTime openedAt,
        @NotNull
        LocalDate dueDate,
        @NotBlank
        String operationId
) {
}
