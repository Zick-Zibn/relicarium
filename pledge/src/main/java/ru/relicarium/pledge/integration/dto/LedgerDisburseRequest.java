package ru.relicarium.pledge.integration.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record LedgerDisburseRequest(
        UUID pledgeId,
        UUID clientId,
        BigDecimal principal,
        BigDecimal interestRate,
        OffsetDateTime openedAt,
        LocalDate dueDate,
        String operationId
) {
}
