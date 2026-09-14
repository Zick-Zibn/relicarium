package ru.relicarium.ledger.application.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.relicarium.ledger.application.dto.PayInterestCommand;
import ru.relicarium.ledger.application.dto.RepayLoanCommand;
import ru.relicarium.ledger.application.service.LoanCashService;
import ru.relicarium.ledger.domain.enums.DocumentType;
import ru.relicarium.ledger.domain.model.Account;
import ru.relicarium.ledger.domain.model.JournalDocument;
import ru.relicarium.ledger.domain.model.JournalLine;
import ru.relicarium.ledger.domain.model.Loan;
import ru.relicarium.ledger.persistence.repository.AccountRepository;
import ru.relicarium.ledger.persistence.repository.JournalDocumentRepository;
import ru.relicarium.ledger.persistence.repository.JournalLineRepository;
import ru.relicarium.ledger.persistence.repository.LoanRepository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class LoanCashServiceImpl implements LoanCashService {

    private final LoanRepository loanRepository;
    private final JournalDocumentRepository documentRepository;
    private final AccountRepository accountRepository;
    private final JournalLineRepository lineRepository;

    @Override
    @Transactional
    public Loan payInterest(PayInterestCommand payInterestCommand) {

        Loan loan = loanRepository.findByPledgeId(payInterestCommand.pledgeId())
                .orElseThrow(() -> new EntityNotFoundException("Loan not found"));

        if (documentRepository.existsByDocumentTypeAndPledgeIdAndOperationId(
                DocumentType.INTEREST_PAYMENT,
                payInterestCommand.pledgeId(),
                payInterestCommand.operationId())) {
            return loan;
        }

        if (loan.getClosedAt() != null) {
            throw new IllegalStateException("The loan has already been closed");
        }

        Account accCash = accountRepository.findById("CASH").orElseThrow(() ->
                new IllegalStateException("Account CASH doesn't exist"));
        Account accIntIncome = accountRepository.findById("INTEREST_INCOME").orElseThrow(() ->
                new IllegalStateException("Account INTEREST_INCOME doesn't exist"));

        JournalDocument document = new JournalDocument();
        document.setOperationId(payInterestCommand.operationId());
        document.setDocumentType(DocumentType.INTEREST_PAYMENT);
        document.setLoan(loan);
        document.setPledgeId(payInterestCommand.pledgeId());
        document = documentRepository.save(document);

        JournalLine journalLineCash = new JournalLine();
        journalLineCash.setJournalDocument(document);
        journalLineCash.setAccount(accCash);
        journalLineCash.setDebit(payInterestCommand.amount());
        journalLineCash.setCredit(BigDecimal.ZERO);

        JournalLine journalLineIntIncome = new JournalLine();
        journalLineIntIncome.setJournalDocument(document);
        journalLineIntIncome.setAccount(accIntIncome);
        journalLineIntIncome.setDebit(BigDecimal.ZERO);
        journalLineIntIncome.setCredit(payInterestCommand.amount());

        if (journalLineCash.getDebit().compareTo(journalLineIntIncome.getCredit()) != 0) {
            throw new IllegalStateException("The debit total does not equal the credit total.");
        }
        lineRepository.save(journalLineCash);
        lineRepository.save(journalLineIntIncome);

        return loan;
    }

    @Override
    @Transactional
    public Loan repay(RepayLoanCommand repayLoanCommand) {

        Loan loan = loanRepository.findByPledgeId(repayLoanCommand.pledgeId())
                .orElseThrow(() -> new EntityNotFoundException("Loan not found"));

        if (documentRepository.existsByDocumentTypeAndPledgeIdAndOperationId(
                DocumentType.REPAYMENT,
                repayLoanCommand.pledgeId(),
                repayLoanCommand.operationId())) {
            return loan;
        }
        if (loan.getClosedAt() != null) {
            throw new IllegalStateException("Loan is closed");
        }

        BigDecimal loanBalance = lineRepository.sumOutstandingPrincipal(loan.getId());

        if (loanBalance == null) {
            loanBalance = BigDecimal.ZERO;
        }

        if (loanBalance.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalStateException("The loan balance is zero.");
        }
        Account accCash = accountRepository.findById("CASH")
                .orElseThrow(() -> new IllegalStateException("Account CASH doesn't exist"));
        Account accLoan = accountRepository.findById("LOANS")
                .orElseThrow(() -> new IllegalStateException("Account LOANS doesn't exist"));
        Account accIntIncome = accountRepository.findById("INTEREST_INCOME")
                .orElseThrow(() -> new IllegalStateException("Account INTEREST_INCOME doesn't exist"));

        JournalDocument document = new JournalDocument();
        document.setOperationId(repayLoanCommand.operationId());
        document.setDocumentType(DocumentType.REPAYMENT);
        document.setLoan(loan);
        document.setPostedAt(repayLoanCommand.paidAt() != null ? repayLoanCommand.paidAt() : OffsetDateTime.now());
        document.setPledgeId(repayLoanCommand.pledgeId());
        document = documentRepository.save(document);

        BigDecimal payAmount = repayLoanCommand.amount();
        ArrayList<JournalLine> arrListLine = new ArrayList<>();
        boolean closeLoans = false;

        JournalLine journalLine;
        if (payAmount.compareTo(loanBalance) < 0) {
            journalLine = new JournalLine();
            journalLine.setJournalDocument(document);
            journalLine.setAccount(accCash);
            journalLine.setDebit(payAmount);
            journalLine.setCredit(BigDecimal.ZERO);
            arrListLine.add(journalLine);

            journalLine = new JournalLine();
            journalLine.setJournalDocument(document);
            journalLine.setAccount(accLoan);
            journalLine.setDebit(BigDecimal.ZERO);
            journalLine.setCredit(payAmount);
            arrListLine.add(journalLine);
        } else if (loanBalance.compareTo(payAmount) == 0) {
            journalLine = new JournalLine();
            journalLine.setJournalDocument(document);
            journalLine.setAccount(accCash);
            journalLine.setDebit(payAmount);
            journalLine.setCredit(BigDecimal.ZERO);
            arrListLine.add(journalLine);

            journalLine = new JournalLine();
            journalLine.setJournalDocument(document);
            journalLine.setAccount(accLoan);
            journalLine.setDebit(BigDecimal.ZERO);
            journalLine.setCredit(loanBalance);
            arrListLine.add(journalLine);
            closeLoans = true;
        } else {
            journalLine = new JournalLine();
            journalLine.setJournalDocument(document);
            journalLine.setAccount(accCash);
            journalLine.setDebit(payAmount);
            journalLine.setCredit(BigDecimal.ZERO);
            arrListLine.add(journalLine);

            journalLine = new JournalLine();
            journalLine.setJournalDocument(document);
            journalLine.setAccount(accLoan);
            journalLine.setDebit(BigDecimal.ZERO);
            journalLine.setCredit(loanBalance);
            arrListLine.add(journalLine);

            journalLine = new JournalLine();
            journalLine.setJournalDocument(document);
            journalLine.setAccount(accIntIncome);
            journalLine.setDebit(BigDecimal.ZERO);
            journalLine.setCredit(payAmount.subtract(loanBalance));
            arrListLine.add(journalLine);
            closeLoans = true;
        }
        lineRepository.saveAll(arrListLine);

        if (closeLoans) {
            loan.setClosedAt(OffsetDateTime.now());
            loanRepository.save(loan);
        }

        return loan;
    }
}
