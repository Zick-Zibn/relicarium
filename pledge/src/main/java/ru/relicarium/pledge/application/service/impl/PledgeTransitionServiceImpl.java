package ru.relicarium.pledge.application.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.relicarium.pledge.application.dto.ApplyPledgeTransitionCommand;
import ru.relicarium.pledge.application.service.PledgeStatusService;
import ru.relicarium.pledge.application.service.PledgeTransitionService;
import ru.relicarium.pledge.domain.enums.PledgeStatusTransition;
import ru.relicarium.pledge.domain.model.Pledge;
import ru.relicarium.pledge.persistence.repository.PledgeRepository;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PledgeTransitionServiceImpl implements PledgeTransitionService {

    private final PledgeRepository pledgeRepository;
    private final PledgeStatusService pledgeStatusService;

    @Override
    @Transactional
    public Pledge apply(UUID pledgeId, ApplyPledgeTransitionCommand command) {

        Pledge pledge = pledgeRepository.findById(pledgeId).orElseThrow(() ->
                new EntityNotFoundException("Pledge not found: " + pledgeId));

        if (command.transition() != PledgeStatusTransition.ENTER_GRACE  && command.transition() != PledgeStatusTransition.MARK_FOR_SALE) {
            throw new IllegalStateException("Transition not allowed via this endpoint: " + command.transition());
        }

        if (command.transition() == PledgeStatusTransition.ENTER_GRACE) {
            LocalDate calcDate = LocalDate.now();
            if (calcDate.isBefore(pledge.getDueDate())) {
                throw new IllegalStateException("Grace period starts only on or after due date");
            }
        }

        pledge = pledgeStatusService.changeStatus(pledge.getId(), command.transition());

        return pledge;
    }
}
