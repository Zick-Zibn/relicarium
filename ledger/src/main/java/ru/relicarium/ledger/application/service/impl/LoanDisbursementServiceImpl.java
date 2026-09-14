package ru.relicarium.ledger.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.relicarium.ledger.application.dto.DisburseLoanCommand;
import ru.relicarium.ledger.application.service.LoanDisbursementService;
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

@Service
@RequiredArgsConstructor
public class LoanDisbursementServiceImpl implements LoanDisbursementService {

    private final LoanRepository loanRepository;
    private final JournalDocumentRepository documentRepository;
    private final JournalLineRepository lineRepository;
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public Loan disburse(DisburseLoanCommand disburseLoanCommand) {

        if (documentRepository.existsByDocumentTypeAndPledgeIdAndOperationId(
                DocumentType.DISBURSEMENT,
                disburseLoanCommand.pledgeId(),
                disburseLoanCommand.operationId())) {
            return loanRepository.findByPledgeId(disburseLoanCommand.pledgeId())
                    .orElseThrow(() -> new IllegalStateException("Document already exist"));
        }
        if (loanRepository.findByPledgeId(disburseLoanCommand.pledgeId()).isPresent()) {
            throw new IllegalStateException("Loan already exists");
        }

        Loan loan = new Loan();
        loan.setClientId(disburseLoanCommand.clientId());
        loan.setPledgeId(disburseLoanCommand.pledgeId());
        loan.setPrincipal(disburseLoanCommand.principal());
        loan.setInterestRate(disburseLoanCommand.interestRate());
        loan.setOpenedAt(disburseLoanCommand.openedAt());
        loan.setDueDate(disburseLoanCommand.dueDate());
        loan.setClosedAt(null);
        loan = loanRepository.save(loan);

        JournalDocument journalDocument = new JournalDocument();
        journalDocument.setDocumentType(DocumentType.DISBURSEMENT);
        journalDocument.setLoan(loan);
        journalDocument.setPledgeId(disburseLoanCommand.pledgeId());
        journalDocument.setOperationId(disburseLoanCommand.operationId());
        journalDocument = documentRepository.save(journalDocument);

        Account accLoan = accountRepository.findById("LOANS").orElseThrow(() ->
                new IllegalStateException("Account LOANS doesn't exist"));
        Account accCash = accountRepository.findById("CASH").orElseThrow(() ->
                new IllegalStateException("Account CASH doesn't exist"));

        JournalLine journalLineLoan = new JournalLine();
        journalLineLoan.setAccount(accLoan);
        journalLineLoan.setJournalDocument(journalDocument);
        journalLineLoan.setDebit(disburseLoanCommand.principal());
        journalLineLoan.setCredit(BigDecimal.ZERO);

        JournalLine journalLineCash = new JournalLine();
        journalLineCash.setAccount(accCash);
        journalLineCash.setJournalDocument(journalDocument);
        journalLineCash.setDebit(BigDecimal.ZERO);
        journalLineCash.setCredit(disburseLoanCommand.principal());

        if (journalLineLoan.getDebit().compareTo(journalLineCash.getCredit()) != 0) {
            throw new IllegalStateException("The debit total does not equal the credit total.");
        }

        lineRepository.save(journalLineLoan);
        lineRepository.save(journalLineCash);

        return loan;
    }
}
