package ru.relicarium.pledge.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record RedeemPledgeRequest(
        @NotBlank
        String operationId,
        @NotNull
        @Positive
        BigDecimal amount,
        OffsetDateTime paidAt
) {
}
