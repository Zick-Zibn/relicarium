package ru.relicarium.pledge.integration;

import ru.relicarium.pledge.integration.dto.LedgerAmountDueResponse;
import ru.relicarium.pledge.integration.dto.LedgerDisburseRequest;
import ru.relicarium.pledge.integration.dto.LedgerLoanResponse;
import ru.relicarium.pledge.integration.dto.LedgerPayInterestRequest;
import ru.relicarium.pledge.integration.dto.LedgerRepayRequest;

import java.time.LocalDate;
import java.util.UUID;

public interface LedgerClient {

    LedgerLoanResponse disburse(LedgerDisburseRequest request);

    LedgerLoanResponse payInterest(LedgerPayInterestRequest request);

    LedgerLoanResponse repay(LedgerRepayRequest request);

    LedgerAmountDueResponse getAmount(UUID pledgeId, LocalDate calculationDate);
}
