package ru.relicarium.ledger.api.controller;

import jakarta.validation.Valid;
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
import ru.relicarium.ledger.application.dto.PayInterestCommand;
import ru.relicarium.ledger.application.dto.RepayLoanCommand;
import ru.relicarium.ledger.application.dto.request.DisburseLoanRequest;
import ru.relicarium.ledger.application.dto.request.PayInterestRequest;
import ru.relicarium.ledger.application.dto.request.RepayLoanRequest;
import ru.relicarium.ledger.application.dto.response.LoanAmountDueResponse;
import ru.relicarium.ledger.application.dto.response.LoanResponse;
import ru.relicarium.ledger.application.service.LoanAmountDueService;
import ru.relicarium.ledger.application.service.LoanCashService;
import ru.relicarium.ledger.application.service.LoanDisbursementService;
import ru.relicarium.ledger.domain.model.Loan;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/loans")
public class LoanController {
    private final LoanAmountDueService loanAmountDueService;
    private final LoanCashService loanCashService;
    private final LoanDisbursementService loanDisbursementService;
    private final LedgerApiMapper ledgerApiMapper;

    public LoanController(LoanAmountDueService loanAmountDueService,
                          LoanCashService loanCashService,
                          LoanDisbursementService loanDisbursementService,
                          LedgerApiMapper ledgerApiMapper) {
        this.loanAmountDueService = loanAmountDueService;
        this.loanCashService = loanCashService;
        this.loanDisbursementService = loanDisbursementService;
        this.ledgerApiMapper = ledgerApiMapper;
    }

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
}
