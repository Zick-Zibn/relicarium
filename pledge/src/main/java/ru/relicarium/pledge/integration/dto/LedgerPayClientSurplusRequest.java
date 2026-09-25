package ru.relicarium.pledge.integration.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record LedgerPayClientSurplusRequest(
        UUID pledgeId,
        String operationId,
        BigDecimal amount,
        OffsetDateTime paidAt
) {
}
