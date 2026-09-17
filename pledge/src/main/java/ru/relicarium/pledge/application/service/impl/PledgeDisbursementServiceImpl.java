package ru.relicarium.pledge.application.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.relicarium.pledge.application.dto.DisbursePledgeCommand;
import ru.relicarium.pledge.application.service.PledgeDisbursementService;
import ru.relicarium.pledge.application.service.PledgeStatusService;
import ru.relicarium.pledge.domain.enums.PledgeStatus;
import ru.relicarium.pledge.domain.enums.PledgeStatusTransition;
import ru.relicarium.pledge.domain.model.Pledge;
import ru.relicarium.pledge.integration.LedgerClient;
import ru.relicarium.pledge.integration.dto.LedgerDisburseRequest;
import ru.relicarium.pledge.persistence.repository.PledgeRepository;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PledgeDisbursementServiceImpl implements PledgeDisbursementService {

    private final PledgeRepository pledgeRepository;
    private final LedgerClient ledgerClient;
    private final PledgeStatusService pledgeStatusService;

    @Override
    @Transactional
    public Pledge disburse(UUID pledgeId, DisbursePledgeCommand command) {

        Pledge pledge = pledgeRepository.findById(pledgeId).orElseThrow(() ->
                new EntityNotFoundException("Pledge not found " + pledgeId));
        if (pledge.getStatus() != PledgeStatus.ACCEPTED) {
            throw new IllegalStateException("Disbursement allowed only for ACCEPTED");
        }
        LedgerDisburseRequest disburseRequest = new LedgerDisburseRequest(
                pledge.getId(),
                pledge.getClient().getId(),
                pledge.getLoanAmount(),
                pledge.getInterestRate(),
                command.openedAt() != null ? command.openedAt() : OffsetDateTime.now(),
                pledge.getDueDate(),
                command.operationId()
        );
        ledgerClient.disburse(disburseRequest);
        pledge = pledgeStatusService.changeStatus(pledge.getId(), PledgeStatusTransition.ACTIVATE);

        return pledge;
    }
}
