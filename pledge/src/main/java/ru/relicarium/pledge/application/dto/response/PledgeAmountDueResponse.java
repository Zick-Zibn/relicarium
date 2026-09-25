package ru.relicarium.pledge.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record PledgeAmountDueResponse(
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
