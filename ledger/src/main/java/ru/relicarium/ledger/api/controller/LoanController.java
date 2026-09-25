package ru.relicarium.ledger.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.relicarium.ledger.api.mapper.LedgerApiMapper;
import ru.relicarium.ledger.application.dto.DisburseLoanCommand;
import ru.relicarium.ledger.application.dto.LoanAmountDueQuery;
import ru.relicarium.ledger.application.dto.LoanAmountDueResult;
import ru.relicarium.ledger.application.dto.PayClientSurplusCommand;
import ru.relicarium.ledger.application.dto.PayInterestCommand;
import ru.relicarium.ledger.application.dto.RepayLoanCommand;
import ru.relicarium.ledger.application.dto.SettleLoanAfterAuctionCommand;
import ru.relicarium.ledger.application.dto.request.DisburseLoanRequest;
import ru.relicarium.ledger.application.dto.request.PayClientSurplusRequest;
import ru.relicarium.ledger.application.dto.request.PayInterestRequest;
import ru.relicarium.ledger.application.dto.request.RepayLoanRequest;
import ru.relicarium.ledger.application.dto.request.SettleLoanAfterAuctionRequest;
import ru.relicarium.ledger.application.dto.response.LoanAmountDueResponse;
import ru.relicarium.ledger.application.dto.response.LoanResponse;
import ru.relicarium.ledger.application.service.ClientSurplusPayoutService;
import ru.relicarium.ledger.application.service.LoanAmountDueService;
import ru.relicarium.ledger.application.service.LoanAuctionSettlementService;
import ru.relicarium.ledger.application.service.LoanCashService;
import ru.relicarium.ledger.application.service.LoanDisbursementService;
import ru.relicarium.ledger.domain.model.Loan;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/loans")
public class LoanController {
    private final LoanAmountDueService loanAmountDueService;
    private final LoanCashService loanCashService;
    private final LoanDisbursementService loanDisbursementService;
    private final LedgerApiMapper ledgerApiMapper;
    private final LoanAuctionSettlementService loanAuctionSettlementService;
    private final ClientSurplusPayoutService clientSurplusPayoutService;

    @GetMapping("/amount-due")
    public ResponseEntity<LoanAmountDueResponse> getAmountDue(
            @RequestParam("pledgeId") UUID pledgeId,
            @RequestParam(value = "asOf", required = false)LocalDate asOf) {
        LoanAmountDueQuery loanAmountDueQuery = new LoanAmountDueQuery(pledgeId, asOf);
        LoanAmountDueResult loanAmountDueResult = loanAmountDueService.getAmountDue(loanAmountDueQuery);
        LoanAmountDueResponse loanAmountDueResponse = ledgerApiMapper.toAmountDueResponse(loanAmountDueResult);

        return ResponseEntity.ok(loanAmountDueResponse);
    }

    @PostMapping("/disbursements")
    public ResponseEntity<LoanResponse> disburse(@Valid @RequestBody DisburseLoanRequest request) {

        DisburseLoanCommand disburseLoanCommand = ledgerApiMapper.toDisburseCommand(request);
        Loan loan = loanDisbursementService.disburse(disburseLoanCommand);
        LoanResponse loanResponse = ledgerApiMapper.toLoanResponse(loan);

        return ResponseEntity.ok(loanResponse);
    }

    @PostMapping("/interest-payments")
    public ResponseEntity<LoanResponse> payInterest(@Valid @RequestBody PayInterestRequest request) {

        PayInterestCommand payInterestCommand = ledgerApiMapper.toPayInterestCommand(request);
        Loan loan = loanCashService.payInterest(payInterestCommand);
        LoanResponse loanResponse = ledgerApiMapper.toLoanResponse(loan);

        return ResponseEntity.ok(loanResponse);
    }

    @PostMapping("/repayments")
    public ResponseEntity<LoanResponse> repay(@Valid @RequestBody RepayLoanRequest request) {

        RepayLoanCommand repayLoanCommand = ledgerApiMapper.toRepayCommand(request);
        Loan loan = loanCashService.repay(repayLoanCommand);
        LoanResponse loanResponse = ledgerApiMapper.toLoanResponse(loan);

        return ResponseEntity.ok(loanResponse);
    }

    @PostMapping("/auction-settlements")
    public ResponseEntity<LoanResponse> auctionSettlements(
            @Valid @RequestBody SettleLoanAfterAuctionRequest request) {

        SettleLoanAfterAuctionCommand command = ledgerApiMapper.toSettleLoanAfterAuctionCommand(request);
        Loan loan = loanAuctionSettlementService.settleAfterAuction(command);

        return ResponseEntity.ok(ledgerApiMapper.toLoanResponse(loan));
    }

    @PostMapping("/client-payouts")
    public ResponseEntity<LoanResponse> clientPayouts(
            @Valid @RequestBody PayClientSurplusRequest request) {

        PayClientSurplusCommand command = ledgerApiMapper.toPayClientSurplusCommand(request);
        Loan loan = clientSurplusPayoutService.payClientSurplus(command);

        return ResponseEntity.ok(ledgerApiMapper.toLoanResponse(loan));
    }
}
