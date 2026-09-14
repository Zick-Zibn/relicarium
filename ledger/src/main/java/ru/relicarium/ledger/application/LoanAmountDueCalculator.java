package ru.relicarium.ledger.application;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class LoanAmountDueCalculator {

    private static final BigDecimal DAYS_IN_YEAR = BigDecimal.valueOf(365);

    private LoanAmountDueCalculator() {
    }

    public static long daysCount(LocalDate openedDate, LocalDate calculationDate) {
        return ChronoUnit.DAYS.between(openedDate, calculationDate);
    }

    public static BigDecimal accruedInterest(
            BigDecimal principalOutstanding,
            BigDecimal annualInterestRate,
            long daysCount) {
        if (principalOutstanding.signum() == 0 || daysCount <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return principalOutstanding
                .multiply(annualInterestRate)
                .multiply(BigDecimal.valueOf(daysCount))
                .divide(DAYS_IN_YEAR, 2, RoundingMode.HALF_UP);
    }

    public static BigDecimal interestDue(
            BigDecimal accruedInterest,
            BigDecimal recognizedInterestIncome) {
        BigDecimal due = accruedInterest.subtract(recognizedInterestIncome);
        if (due.signum() < 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return due.setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal totalDue(
            BigDecimal principalOutstanding,
            BigDecimal interestDue) {
        return principalOutstanding.add(interestDue).setScale(2, RoundingMode.HALF_UP);
    }
}
