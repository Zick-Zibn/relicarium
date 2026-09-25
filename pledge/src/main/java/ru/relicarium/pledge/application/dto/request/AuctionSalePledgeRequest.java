package ru.relicarium.pledge.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record AuctionSalePledgeRequest(
        @NotBlank
        String operationId,
        @NotNull
        @Positive
        BigDecimal saleProceeds
) {
}
