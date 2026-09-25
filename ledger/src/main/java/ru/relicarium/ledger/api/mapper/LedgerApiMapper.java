package ru.relicarium.ledger.api.mapper;

import org.springframework.stereotype.Component;
import ru.relicarium.ledger.application.dto.DisburseLoanCommand;
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
import ru.relicarium.ledger.domain.model.Loan;

@Component
public class LedgerApiMapper {

    public DisburseLoanCommand toDisburseCommand(DisburseLoanRequest request) {
        return new DisburseLoanCommand(
                request.pledgeId(),
                request.clientId(),
                request.principal(),
                request.interestRate(),
                request.openedAt(),
                request.dueDate(),
                request.operationId());
    }

    public PayInterestCommand toPayInterestCommand(PayInterestRequest request) {
        return new PayInterestCommand(
                request.pledgeId(),
                request.amount(),
                request.operationId(),
                request.paidAt());
    }

    public RepayLoanCommand toRepayCommand(RepayLoanRequest request) {
        return new RepayLoanCommand(
                request.pledgeId(),
                request.amount(),
                request.operationId(),
                request.paidAt());
    }

    public LoanResponse toLoanResponse(Loan loan) {
        return new LoanResponse(
                loan.getId(),
                loan.getPledgeId(),
                loan.getClientId(),
                loan.getPrincipal(),
                loan.getInterestRate(),
                loan.getOpenedAt(),
                loan.getDueDate(),
                loan.getClosedAt());
    }

    public LoanAmountDueResponse toAmountDueResponse(LoanAmountDueResult amountDueSnapshot) {
        return new LoanAmountDueResponse(
                amountDueSnapshot.pledgeId(),
                amountDueSnapshot.asOf(),
                amountDueSnapshot.principalOutstanding(),
                amountDueSnapshot.interestAccrued(),
                amountDueSnapshot.interestRecognized(),
                amountDueSnapshot.interestDue(),
                amountDueSnapshot.totalDue(),
                amountDueSnapshot.closed());
    }
    public SettleLoanAfterAuctionCommand toSettleLoanAfterAuctionCommand(SettleLoanAfterAuctionRequest request) {
        return new SettleLoanAfterAuctionCommand(
                request.pledgeId(),
                request.operationId(),
                request.saleProceeds(),
                request.settleAt()
        );
    }

    public PayClientSurplusCommand toPayClientSurplusCommand(PayClientSurplusRequest request) {
        return new PayClientSurplusCommand(
                request.pledgeId(),
                request.operationId(),
                request.amount(),
                request.paidAt()
        );
    }
}
