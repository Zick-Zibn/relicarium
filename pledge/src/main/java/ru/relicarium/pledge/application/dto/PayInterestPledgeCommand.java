package ru.relicarium.pledge.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record PayInterestPledgeCommand(
        @NotBlank
        String operationId,
        @NotNull
        @Positive
        BigDecimal amount,
        OffsetDateTime paidAt
) {
}
