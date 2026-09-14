package ru.relicarium.ledger.application;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;
class LoanAmountDueCalculatorTest {
    private static final BigDecimal PRINCIPAL_OUTSTANDING = new BigDecimal("30000.00");
    private static final BigDecimal ANNUAL_INTEREST_RATE = new BigDecimal("0.1200");
    @Test
    void daysCount_onOpenDay_isZero() {
        LocalDate openedDate = LocalDate.of(2026, 1, 15);
        assertEquals(0, LoanAmountDueCalculator.daysCount(openedDate, openedDate));
    }
    @Test
    void daysCount_thirtyDaysBetweenDates() {
        LocalDate openedDate = LocalDate.of(2026, 1, 1);
        LocalDate calculationDate = LocalDate.of(2026, 1, 31);
        assertEquals(30, LoanAmountDueCalculator.daysCount(openedDate, calculationDate));
    }
    @Test
    void accruedInterest_thirtyDays_matchesExpected() {
        long daysCount = 30;
        BigDecimal accrued = LoanAmountDueCalculator.accruedInterest(
                PRINCIPAL_OUTSTANDING, ANNUAL_INTEREST_RATE, daysCount);
        // 30000 × 0.12 × 30 / 365 = 295.89
        assertEquals(new BigDecimal("295.89"), accrued);
    }
    @Test
    void accruedInterest_zeroDays_isZero() {
        BigDecimal accrued = LoanAmountDueCalculator.accruedInterest(
                PRINCIPAL_OUTSTANDING, ANNUAL_INTEREST_RATE, 0);
        assertEquals(new BigDecimal("0.00"), accrued);
    }
    @Test
    void interestDue_subtractsRecognized_neverNegative() {
        BigDecimal accruedInterest = new BigDecimal("295.89");
        assertEquals(new BigDecimal("195.89"),
                LoanAmountDueCalculator.interestDue(
                        accruedInterest, new BigDecimal("100.00")));
        assertEquals(new BigDecimal("0.00"),
                LoanAmountDueCalculator.interestDue(
                        accruedInterest, new BigDecimal("500.00")));
    }
    @Test
    void totalDue_isPrincipalOutstandingPlusInterestDue() {
        assertEquals(new BigDecimal("30295.89"),
                LoanAmountDueCalculator.totalDue(
                        PRINCIPAL_OUTSTANDING, new BigDecimal("295.89")));
    }
    @Test
    void fullScenario_noRecognizedInterest() {
        LocalDate openedDate = LocalDate.of(2026, 1, 1);
        LocalDate calculationDate = LocalDate.of(2026, 1, 31);
        long daysCount = LoanAmountDueCalculator.daysCount(openedDate, calculationDate);
        BigDecimal accruedInterest = LoanAmountDueCalculator.accruedInterest(
                PRINCIPAL_OUTSTANDING, ANNUAL_INTEREST_RATE, daysCount);
        BigDecimal recognizedInterestIncome = BigDecimal.ZERO.setScale(2);
        BigDecimal interestDue = LoanAmountDueCalculator.interestDue(
                accruedInterest, recognizedInterestIncome);
        BigDecimal totalDue = LoanAmountDueCalculator.totalDue(
                PRINCIPAL_OUTSTANDING, interestDue);
        assertEquals(new BigDecimal("295.89"), accruedInterest);
        assertEquals(new BigDecimal("295.89"), interestDue);
        assertEquals(new BigDecimal("30295.89"), totalDue);
    }
}
