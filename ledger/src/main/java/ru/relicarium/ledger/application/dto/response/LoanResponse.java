package ru.relicarium.ledger.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record LoanResponse(
        UUID id,
        UUID pledgeId,
        UUID clientId,
        BigDecimal principal,
        BigDecimal interestRate,
        OffsetDateTime openedAt,
        LocalDate dueDate,
        OffsetDateTime closedAt
) {
}
