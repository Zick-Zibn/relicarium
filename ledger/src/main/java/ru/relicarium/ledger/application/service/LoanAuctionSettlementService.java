package ru.relicarium.ledger.application.service;

import ru.relicarium.ledger.application.dto.SettleLoanAfterAuctionCommand;
import ru.relicarium.ledger.domain.model.Loan;

public interface LoanAuctionSettlementService {

    Loan settleAfterAuction(SettleLoanAfterAuctionCommand command);
}
