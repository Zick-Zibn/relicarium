package ru.relicarium.pledge.application.service;

import ru.relicarium.pledge.application.dto.ApplyPledgeTransitionCommand;
import ru.relicarium.pledge.domain.model.Pledge;

import java.util.UUID;

public interface PledgeTransitionService {

    Pledge apply(UUID pledgeId, ApplyPledgeTransitionCommand command);
}
