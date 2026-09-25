package ru.relicarium.ledger.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record SettleLoanAfterAuctionRequest(
        @NotNull
        UUID pledgeId,
        @NotBlank
        String operationId,
        @NotNull
        @Positive
        BigDecimal saleProceeds,
        OffsetDateTime settleAt
) {
}
