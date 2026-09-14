package ru.relicarium.ledger.application.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.relicarium.ledger.application.dto.LoanAmountDueQuery;
import ru.relicarium.ledger.application.dto.LoanAmountDueResult;
import ru.relicarium.ledger.application.service.LoanAmountDueService;
import ru.relicarium.ledger.domain.model.Loan;
import ru.relicarium.ledger.persistence.repository.JournalLineRepository;
import ru.relicarium.ledger.persistence.repository.LoanRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class LoanAmountDueServiceImpl implements LoanAmountDueService {

    private final LoanRepository loanRepository;
    private final JournalLineRepository lineRepository;

    @Override
    @Transactional(readOnly = true)
    public LoanAmountDueResult getAmountDue(LoanAmountDueQuery loanAmountDueQuery) {

        LocalDate calculationDate = loanAmountDueQuery.asOf() != null ? loanAmountDueQuery.asOf() : LocalDate.now();
        Loan loan = loanRepository.findByPledgeId(loanAmountDueQuery.pledgeId())
                .orElseThrow(() -> new EntityNotFoundException("Loan not found"));

        LocalDate openedDate = loan.getOpenedAt()
                .atZoneSameInstant(ZoneId.of("Europe/Moscow")).toLocalDate();
        boolean closed = loan.getClosedAt() != null;
        BigDecimal annualInterestRate = loan.getInterestRate();

        if (calculationDate.isBefore(openedDate)) {
            throw new IllegalArgumentException("Counting is not permitted prior to issuance.");
        }

        if (closed) {
            return LoanAmountDueResult.initAllValueNull(loanAmountDueQuery.pledgeId(), calculationDate, closed);
        }

        BigDecimal loanBalance = lineRepository.sumOutstandingPrincipal(loan.getId());
        loanBalance = loanBalance == null ? BigDecimal.ZERO : loanBalance;

        if (loanBalance.compareTo(BigDecimal.ZERO) == 0 && loan.getClosedAt() == null) {
            return LoanAmountDueResult.initAllValueNull(loanAmountDueQuery.pledgeId(), calculationDate, closed);
        }

        BigDecimal recognized = lineRepository.sumRecognizedInterestIncome(loan.getId());
        recognized = recognized == null ? BigDecimal.ZERO : recognized;

        long daysCount = ChronoUnit.DAYS.between(openedDate, calculationDate);

        BigDecimal accruedInterest;

        if (loanBalance.compareTo(BigDecimal.ZERO) == 0 || daysCount <= 0) {
            accruedInterest = BigDecimal.ZERO;
        } else {
            accruedInterest = loanBalance
                    .multiply(annualInterestRate)
                    .multiply(BigDecimal.valueOf(daysCount))
                    .divide(BigDecimal.valueOf(365), 2, RoundingMode.HALF_UP);
        }

        BigDecimal interestDue = accruedInterest.subtract(recognized);

        if (interestDue.compareTo(BigDecimal.ZERO) < 0) {
            interestDue = BigDecimal.ZERO;
        }
        BigDecimal totalDue = loanBalance.add(interestDue);

        return new LoanAmountDueResult(
                loanAmountDueQuery.pledgeId(),
                calculationDate,
                loanBalance,
                accruedInterest,
                recognized,
                interestDue,
                totalDue,
                closed);
    }
}
