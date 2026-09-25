package ru.relicarium.pledge.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record AuctionSalePledgeCommand(
        @NotBlank
        String operationId,
        @NotNull
        @Positive
        BigDecimal saleProceeds
) {
}
