package ru.relicarium.pledge.integration.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record AuctionCompleteLotRequest(
        UUID pledgeId,
        String operationId,
        String finalStatus,
        BigDecimal saleProceeds
) {
}
