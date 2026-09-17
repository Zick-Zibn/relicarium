package ru.relicarium.pledge.integration.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record LedgerAmountDueResponse(
        UUID pledgeId,
        LocalDate asOf,
        BigDecimal principalOutstanding,
        BigDecimal interestAccrued,
        BigDecimal interestRecognized,
        BigDecimal interestDue,
        BigDecimal totalDue,
        boolean closed
) {
}
