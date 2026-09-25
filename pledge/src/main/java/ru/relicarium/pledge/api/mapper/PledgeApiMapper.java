package ru.relicarium.pledge.api.mapper;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import ru.relicarium.pledge.application.dto.AcceptPledgeCommand;
import ru.relicarium.pledge.application.dto.ApplyPledgeTransitionCommand;
import ru.relicarium.pledge.application.dto.AuctionReturnPledgeCommand;
import ru.relicarium.pledge.application.dto.AuctionSalePledgeCommand;
import ru.relicarium.pledge.application.dto.DisbursePledgeCommand;
import ru.relicarium.pledge.application.dto.PayClientSurplusPledgeCommand;
import ru.relicarium.pledge.application.dto.PayInterestPledgeCommand;
import ru.relicarium.pledge.application.dto.RedeemPledgeCommand;
import ru.relicarium.pledge.application.dto.SendToAuctionPledgeCommand;
import ru.relicarium.pledge.application.dto.request.ApplyPledgeTransitionRequest;
import ru.relicarium.pledge.application.dto.request.AuctionReturnPledgeRequest;
import ru.relicarium.pledge.application.dto.request.AuctionSalePledgeRequest;
import ru.relicarium.pledge.application.dto.request.CreatePledgeRequest;
import ru.relicarium.pledge.application.dto.request.DisbursePledgeRequest;
import ru.relicarium.pledge.application.dto.request.PayClientSurplusPledgeRequest;
import ru.relicarium.pledge.application.dto.request.PayInterestPledgeRequest;
import ru.relicarium.pledge.application.dto.request.RedeemPledgeRequest;
import ru.relicarium.pledge.application.dto.request.SendToAuctionPledgeRequest;
import ru.relicarium.pledge.application.dto.response.PledgeAmountDueResponse;
import ru.relicarium.pledge.application.dto.response.PledgePageResponse;
import ru.relicarium.pledge.application.dto.response.PledgeResponse;
import ru.relicarium.pledge.domain.model.Pledge;
import ru.relicarium.pledge.integration.dto.LedgerAmountDueResponse;

@Component
public class PledgeApiMapper {

    public AcceptPledgeCommand toCommand(CreatePledgeRequest createPledgeRequest) {
        return new AcceptPledgeCommand(
                createPledgeRequest.fullName(),
                createPledgeRequest.phone(),
                createPledgeRequest.passportSeries(),
                createPledgeRequest.passportNumber(),
                createPledgeRequest.itemName(),
                createPledgeRequest.description(),
                createPledgeRequest.category(),
                createPledgeRequest.estimatedValue(),
                createPledgeRequest.loanAmount(),
                createPledgeRequest.interestRate(),
                createPledgeRequest.termDays()
        );
    }

    public PledgeResponse toResponse(Pledge pledge) {
        return new PledgeResponse(
                pledge.getId(),
                pledge.getStatus(),
                pledge.getLoanAmount(),
                pledge.getInterestRate(),
                pledge.getTermDays(),
                pledge.getAcceptedAt(),
                pledge.getDueDate(),
                pledge.getClient().getId(),
                pledge.getItem().getId(),
                pledge.getItem().getName(),
                pledge.getItem().getStorageStatus(),
                pledge.getRedeemedAt()
        );
    }

    public DisbursePledgeCommand toDisburseCommand(DisbursePledgeRequest request) {
        return new DisbursePledgeCommand(
                request.operationId(),
                request.openedAt()
        );
    }

    public RedeemPledgeCommand toRedeemCommand(RedeemPledgeRequest request) {
        return new RedeemPledgeCommand(
                request.operationId(),
                request.amount(),
                request.paidAt()
        );
    }

    public PayInterestPledgeCommand toPayInterestCommand(PayInterestPledgeRequest request) {
        return new PayInterestPledgeCommand(
                request.operationId(),
                request.amount(),
                request.paidAt()
        );
    }

    public ApplyPledgeTransitionCommand toApplyPledgeTransitionCommand(ApplyPledgeTransitionRequest request) {

        return new ApplyPledgeTransitionCommand(request.transition());
    }

    public SendToAuctionPledgeCommand toSendToAuctionPledgeCommand(SendToAuctionPledgeRequest request) {

        return new SendToAuctionPledgeCommand(request.operationId());
    }

    public AuctionSalePledgeCommand toAuctionSalePledgeCommand(AuctionSalePledgeRequest request) {

        return new AuctionSalePledgeCommand(
                request.operationId(),
                request.saleProceeds());
    }

    public AuctionReturnPledgeCommand toAuctionReturnPledgeCommand(AuctionReturnPledgeRequest request) {

        return new AuctionReturnPledgeCommand(request.operationId());
    }

    public PledgeAmountDueResponse toAmountDueResponse(LedgerAmountDueResponse ledgerAmountDueResponse) {
        return new PledgeAmountDueResponse(
                ledgerAmountDueResponse.pledgeId(),
                ledgerAmountDueResponse.asOf(),
                ledgerAmountDueResponse.principalOutstanding(),
                ledgerAmountDueResponse.interestAccrued(),
                ledgerAmountDueResponse.interestRecognized(),
                ledgerAmountDueResponse.interestDue(),
                ledgerAmountDueResponse.totalDue(),
                ledgerAmountDueResponse.closed()
        );
    }

    public PayClientSurplusPledgeCommand toPayClientSurplusPledgeCommand(PayClientSurplusPledgeRequest request) {
        return new PayClientSurplusPledgeCommand(
                request.operationId(),
                request.amount(),
                request.paidAt()
        );
    }

    public PledgePageResponse toPageResponse(Page<Pledge> page) {
        return new PledgePageResponse(
                page.getContent().stream().map(this::toResponse).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
