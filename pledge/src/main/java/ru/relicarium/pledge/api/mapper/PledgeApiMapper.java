package ru.relicarium.pledge.api.mapper;

import org.springframework.stereotype.Component;
import ru.relicarium.pledge.application.dto.AcceptPledgeCommand;
import ru.relicarium.pledge.application.dto.ApplyPledgeTransitionCommand;
import ru.relicarium.pledge.application.dto.DisbursePledgeCommand;
import ru.relicarium.pledge.application.dto.PayInterestPledgeCommand;
import ru.relicarium.pledge.application.dto.RedeemPledgeCommand;
import ru.relicarium.pledge.application.dto.request.ApplyPledgeTransitionRequest;
import ru.relicarium.pledge.application.dto.request.CreatePledgeRequest;
import ru.relicarium.pledge.application.dto.request.DisbursePledgeRequest;
import ru.relicarium.pledge.application.dto.request.PayInterestPledgeRequest;
import ru.relicarium.pledge.application.dto.request.RedeemPledgeRequest;
import ru.relicarium.pledge.application.dto.response.PledgeResponse;
import ru.relicarium.pledge.domain.model.Pledge;

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
                pledge.getItem().getName()
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
}
