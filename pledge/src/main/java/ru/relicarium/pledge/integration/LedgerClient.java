package ru.relicarium.pledge.integration;

import ru.relicarium.pledge.integration.dto.LedgerAmountDueResponse;
import ru.relicarium.pledge.integration.dto.LedgerDisburseRequest;
import ru.relicarium.pledge.integration.dto.LedgerLoanResponse;
import ru.relicarium.pledge.integration.dto.LedgerPayClientSurplusRequest;
import ru.relicarium.pledge.integration.dto.LedgerPayInterestRequest;
import ru.relicarium.pledge.integration.dto.LedgerRepayRequest;
import ru.relicarium.pledge.integration.dto.LedgerSettleAfterAuctionRequest;

import java.time.LocalDate;
import java.util.UUID;

public interface LedgerClient {

    LedgerLoanResponse disburse(LedgerDisburseRequest httpRequest);
    LedgerLoanResponse payInterest(LedgerPayInterestRequest httpRequest);
    LedgerLoanResponse repay(LedgerRepayRequest request);
    LedgerAmountDueResponse getAmount(UUID pledgeId, LocalDate calculationDate);
    LedgerLoanResponse settleAfterAuction(LedgerSettleAfterAuctionRequest httpRequest);
    LedgerLoanResponse payClientSurplus(LedgerPayClientSurplusRequest httpRequest);
}
