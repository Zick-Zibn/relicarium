package ru.relicarium.pledge.integration.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record LedgerSettleAfterAuctionRequest(
        UUID pledgeId,
        BigDecimal saleProceeds,
        String operationId,
        OffsetDateTime settleAt
) {
}
