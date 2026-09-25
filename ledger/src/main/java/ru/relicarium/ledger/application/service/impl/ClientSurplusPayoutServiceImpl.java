package ru.relicarium.ledger.application.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.relicarium.ledger.application.dto.PayClientSurplusCommand;
import ru.relicarium.ledger.application.service.ClientSurplusPayoutService;
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

@Service
@RequiredArgsConstructor
public class ClientSurplusPayoutServiceImpl implements ClientSurplusPayoutService {

    private final LoanRepository loanRepository;
    private final JournalDocumentRepository journalDocumentRepository;
    private final JournalLineRepository journalLineRepository;
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public Loan payClientSurplus(PayClientSurplusCommand command) {

        Loan loan = loanRepository.findByPledgeId(command.pledgeId()).orElseThrow(() ->
                new EntityNotFoundException("Not found loan by pledgeId: " + command.pledgeId()));
        if (loan.getClosedAt() == null) {
            throw new IllegalStateException("Loan not closed");
        }
        if (journalDocumentRepository.existsByDocumentTypeAndPledgeIdAndOperationId(
                DocumentType.CLIENT_PAYOUT,
                command.pledgeId(),
                command.operationId())) {
            return loan;
        }
        BigDecimal balance = journalLineRepository.sumClientPayableBalanceByPledgeId(command.pledgeId());

        if (balance == null) {
            balance = BigDecimal.ZERO;
        }

        if (balance.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalStateException("Client balance is zero");
        }
        if (command.amount().compareTo(balance) > 0) {
            throw new IllegalStateException("Amount over balance");
        }

        JournalDocument document = new JournalDocument();
        document.setPledgeId(command.pledgeId());
        document.setLoan(loan);
        document.setOperationId(command.operationId());
        document.setDocumentType(DocumentType.CLIENT_PAYOUT);
        document.setPostedAt(command.paidAt() != null ? command.paidAt() : OffsetDateTime.now());
        journalDocumentRepository.save(document);

        Account accIntClientPay = accountRepository.findById("CLIENT_PAYABLE")
                .orElseThrow(() -> new IllegalStateException("Account CLIENT_PAYABLE doesn't exist"));
        Account accCash = accountRepository.findById("CASH")
                .orElseThrow(() -> new IllegalStateException("Account CASH doesn't exist"));

        JournalLine journalLine = new JournalLine();
        journalLine.setJournalDocument(document);
        journalLine.setAccount(accIntClientPay);
        journalLine.setDebit(command.amount());
        journalLine.setCredit(BigDecimal.ZERO);
        journalLineRepository.save(journalLine);

        journalLine = new JournalLine();
        journalLine.setJournalDocument(document);
        journalLine.setAccount(accCash);
        journalLine.setDebit(BigDecimal.ZERO);
        journalLine.setCredit(command.amount());
        journalLineRepository.save(journalLine);

        return loan;
    }
}
