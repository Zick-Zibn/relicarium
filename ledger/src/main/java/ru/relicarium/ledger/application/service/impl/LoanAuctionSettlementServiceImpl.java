package ru.relicarium.ledger.application.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.relicarium.ledger.application.dto.LoanAmountDueQuery;
import ru.relicarium.ledger.application.dto.LoanAmountDueResult;
import ru.relicarium.ledger.application.dto.SettleLoanAfterAuctionCommand;
import ru.relicarium.ledger.application.service.LoanAmountDueService;
import ru.relicarium.ledger.application.service.LoanAuctionSettlementService;
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
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class LoanAuctionSettlementServiceImpl implements LoanAuctionSettlementService {

    private final LoanRepository loanRepository;
    private final JournalDocumentRepository documentRepository;
    private final JournalLineRepository journalLineRepository;
    private final LoanAmountDueService amountDueService;
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public Loan settleAfterAuction(SettleLoanAfterAuctionCommand command) {

        Loan loan = loanRepository.findByPledgeId(command.pledgeId()).orElseThrow(() ->
                new EntityNotFoundException("Loan not found by pledgeId: " + command.pledgeId()));
        if (documentRepository.existsByDocumentTypeAndPledgeIdAndOperationId(
                DocumentType.AUCTION_SETTLEMENT,
                command.pledgeId(),
                command.operationId())) {
            return loan;
        }
        if (loan.getClosedAt() != null) {
            throw new IllegalStateException("Loan is closed");
        }
        OffsetDateTime settledAtEffective = command.settledAt() != null ? command.settledAt() : OffsetDateTime.now();
        LocalDate calculationDate = settledAtEffective
                .atZoneSameInstant(ZoneId.of("Europe/Moscow")).toLocalDate();

        LoanAmountDueResult result = amountDueService
                .getAmountDue(new LoanAmountDueQuery(command.pledgeId(), calculationDate));
        BigDecimal principalOutstanding = result.principalOutstanding();
        BigDecimal interestDue = result.interestDue();
        BigDecimal totalDue = result.totalDue();

        if (command.saleProceeds().compareTo(totalDue) < 0 ) {
            throw new IllegalStateException("Sale proceeds insufficient to cover total due");
        }
        BigDecimal clientSurplus = command.saleProceeds().subtract(totalDue);

        Account accCash = accountRepository.findById("CASH")
                .orElseThrow(() -> new IllegalStateException("Account CASH doesn't exist"));
        Account accLoan = accountRepository.findById("LOANS")
                .orElseThrow(() -> new IllegalStateException("Account LOANS doesn't exist"));
        Account accIntIncome = accountRepository.findById("INTEREST_INCOME")
                .orElseThrow(() -> new IllegalStateException("Account INTEREST_INCOME doesn't exist"));
        Account accIntClientPay = accountRepository.findById("CLIENT_PAYABLE")
                .orElseThrow(() -> new IllegalStateException("Account CLIENT_PAYABLE doesn't exist"));

        if (command.saleProceeds().compareTo(principalOutstanding.add(interestDue).add(clientSurplus)) != 0) {
            throw new IllegalStateException("Debit amounts must equal credit amounts");
        }
        JournalDocument journalDocument = new JournalDocument();
        journalDocument.setLoan(loan);
        journalDocument.setOperationId(command.operationId());
        journalDocument.setDocumentType(DocumentType.AUCTION_SETTLEMENT);
        journalDocument.setPledgeId(command.pledgeId());
        journalDocument.setPostedAt(settledAtEffective);
        journalDocument = documentRepository.save(journalDocument);

        ArrayList<JournalLine> arrListLine = new ArrayList<>();

        JournalLine journalLine = new JournalLine();
        journalLine.setJournalDocument(journalDocument);
        journalLine.setAccount(accCash);
        journalLine.setDebit(command.saleProceeds());
        journalLine.setCredit(BigDecimal.ZERO);
        arrListLine.add(journalLine);

        if (principalOutstanding.compareTo(BigDecimal.ZERO) > 0) {
            journalLine = new JournalLine();
            journalLine.setJournalDocument(journalDocument);
            journalLine.setAccount(accLoan);
            journalLine.setDebit(BigDecimal.ZERO);
            journalLine.setCredit(principalOutstanding);
            arrListLine.add(journalLine);
        }

        if (interestDue.compareTo(BigDecimal.ZERO) > 0) {
            journalLine = new JournalLine();
            journalLine.setJournalDocument(journalDocument);
            journalLine.setAccount(accIntIncome);
            journalLine.setDebit(BigDecimal.ZERO);
            journalLine.setCredit(interestDue);
            arrListLine.add(journalLine);
        }

        if (clientSurplus.compareTo(BigDecimal.ZERO) > 0) {
            journalLine = new JournalLine();
            journalLine.setJournalDocument(journalDocument);
            journalLine.setAccount(accIntClientPay);
            journalLine.setDebit(BigDecimal.ZERO);
            journalLine.setCredit(clientSurplus);
            arrListLine.add(journalLine);
        }
        journalLineRepository.saveAll(arrListLine);

        loan.setClosedAt(settledAtEffective);
        loan = loanRepository.save(loan);

        return loan;
    }
}
