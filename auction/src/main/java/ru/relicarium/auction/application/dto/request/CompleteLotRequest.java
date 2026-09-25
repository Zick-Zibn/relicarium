package ru.relicarium.auction.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.relicarium.auction.domain.enums.LotStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record CompleteLotRequest(
        @NotNull
        UUID pledgeId,
        @NotBlank
        String operationId,
        @NotNull
        LotStatus finalStatus,
        BigDecimal saleProceeds
) {
}
