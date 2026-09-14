package ru.relicarium.ledger.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record LoanAmountDueResult(
        UUID pledgeId,
        LocalDate asOf,
        BigDecimal principalOutstanding,
        BigDecimal interestAccrued,
        BigDecimal interestRecognized,
        BigDecimal interestDue,
        BigDecimal totalDue,
        boolean closed
)
{
    public static LoanAmountDueResult initAllValueNull(UUID pledgeId, LocalDate asOf, boolean closed) {

        return new LoanAmountDueResult(
                pledgeId,
                asOf,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                closed);
    }

}
