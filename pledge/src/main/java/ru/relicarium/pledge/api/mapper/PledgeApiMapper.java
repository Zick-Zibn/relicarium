package ru.relicarium.pledge.api.mapper;

import org.springframework.stereotype.Component;
import ru.relicarium.pledge.application.dto.AcceptPledgeCommand;
import ru.relicarium.pledge.application.dto.request.CreatePledgeRequest;
import ru.relicarium.pledge.application.dto.response.PledgeResponse;
import ru.relicarium.pledge.domain.model.Pledge;

@Component
public class PledgeApiMapper {

     public AcceptPledgeCommand toCommand(CreatePledgeRequest createPledgeRequest) {
         return  new AcceptPledgeCommand(
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
                 createPledgeRequest.termDays());
     }

     public PledgeResponse toResponse(Pledge pledge) {
        return  new PledgeResponse(
                pledge.getId(),
                pledge.getStatus(),
                pledge.getLoanAmount(),
                pledge.getInterestRate(),
                pledge.getTermDays(),
                pledge.getAcceptedAt(),
                pledge.getDueDate(),
                pledge.getClient().getId(),
                pledge.getItem().getId(),
                pledge.getItem().getName());
     }
}
