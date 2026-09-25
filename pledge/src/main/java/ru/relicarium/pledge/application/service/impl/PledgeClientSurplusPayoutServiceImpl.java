package ru.relicarium.pledge.application.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.relicarium.pledge.application.dto.PayClientSurplusPledgeCommand;
import ru.relicarium.pledge.application.service.PledgeClientSurplusPayoutService;
import ru.relicarium.pledge.domain.enums.PledgeStatus;
import ru.relicarium.pledge.domain.model.Pledge;
import ru.relicarium.pledge.integration.LedgerClient;
import ru.relicarium.pledge.integration.dto.LedgerPayClientSurplusRequest;
import ru.relicarium.pledge.persistence.repository.PledgeRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PledgeClientSurplusPayoutServiceImpl implements PledgeClientSurplusPayoutService {

    private final PledgeRepository pledgeRepository;
    private final LedgerClient ledgerClient;

    @Override
    @Transactional
    public Pledge payClientSurplus(UUID pledgeId, PayClientSurplusPledgeCommand command) {

        Pledge pledge = pledgeRepository.findById(pledgeId).orElseThrow(() ->
                new EntityNotFoundException("Pledge not found: " + pledgeId));

        if (pledge.getStatus() != PledgeStatus.SOLD) {
            throw new IllegalStateException("Surplus is only SOLD pledge status");
        }

        LedgerPayClientSurplusRequest ledgerPayClientSurplusRequest = new LedgerPayClientSurplusRequest(
                pledge.getId(),
                command.operationId(),
                command.amount(),
                command.paidAt()
        );

        ledgerClient.payClientSurplus(ledgerPayClientSurplusRequest);

        return pledge;
    }
}
