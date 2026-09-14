package ru.relicarium.pledge.application.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.relicarium.pledge.application.service.PledgeStatusService;
import ru.relicarium.pledge.domain.enums.PledgeStatus;
import ru.relicarium.pledge.domain.enums.PledgeStatusTransition;
import ru.relicarium.pledge.domain.model.Pledge;
import ru.relicarium.pledge.domain.state.PledgeStateMachine;
import ru.relicarium.pledge.persistence.repository.PledgeRepository;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PledgeStatusServiceImpl implements PledgeStatusService {

    private final PledgeRepository pledgeRepository;
    private final PledgeStateMachine stateMachine = new PledgeStateMachine();

    @Override
    @Transactional
    public Pledge changeStatus(UUID pledgeId, PledgeStatusTransition statusTransition) {

        Pledge pledge = pledgeRepository.findById(pledgeId)
                .orElseThrow(() -> new EntityNotFoundException("Pledge not found: " + pledgeId));

        PledgeStatus newStatus = stateMachine.transition(pledge.getStatus(), statusTransition);

        pledge.setStatus(newStatus);

        if (newStatus == PledgeStatus.REDEEMED) {
            pledge.setRedeemedAt(OffsetDateTime.now());
        }
        pledge = pledgeRepository.save(pledge);

        return pledge;
    }
}
