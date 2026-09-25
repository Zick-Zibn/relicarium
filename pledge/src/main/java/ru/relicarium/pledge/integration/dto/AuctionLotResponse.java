package ru.relicarium.pledge.integration.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record AuctionLotResponse(
        UUID id,
        UUID pledgeId,
        String status,
        OffsetDateTime createdAt,
        BigDecimal saleProceeds
) {
}
