package ru.relicarium.pledge.integration.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record LedgerRepayRequest(
        UUID pledgeId,
        BigDecimal amount,
        String operationId,
        OffsetDateTime paidAt
) {
}
