package ru.relicarium.pledge.application.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.relicarium.pledge.application.dto.PayInterestPledgeCommand;
import ru.relicarium.pledge.application.service.PledgeInterestPaymentService;
import ru.relicarium.pledge.domain.enums.PledgeStatus;
import ru.relicarium.pledge.domain.model.Pledge;
import ru.relicarium.pledge.integration.LedgerClient;
import ru.relicarium.pledge.integration.dto.LedgerPayInterestRequest;
import ru.relicarium.pledge.persistence.repository.PledgeRepository;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PledgeInterestPaymentServiceImpl implements PledgeInterestPaymentService {

    private final PledgeRepository pledgeRepository;
    private final LedgerClient ledgerClient;

    @Override
    @Transactional
    public Pledge payInterest(UUID pledgeId, PayInterestPledgeCommand command) {

        Pledge pledge = pledgeRepository.findById(pledgeId).orElseThrow(() ->
                new EntityNotFoundException("Pledge not found: " + pledgeId));
        if (pledge.getStatus() != PledgeStatus.ACTIVE && pledge.getStatus() != PledgeStatus.GRACE) {
            throw new IllegalStateException("Interest payment allowed only for ACTIVE or GRACE");
        }
        LedgerPayInterestRequest payInterestRequest = new LedgerPayInterestRequest(
                pledge.getId(),
                command.amount(),
                command.operationId(),
                command.paidAt() != null ? command.paidAt() : OffsetDateTime.now()
        );
        ledgerClient.payInterest(payInterestRequest);

        return pledge;
    }
}
