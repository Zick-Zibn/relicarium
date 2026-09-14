package ru.relicarium.pledge.application.service;

import ru.relicarium.pledge.domain.enums.PledgeStatusTransition;
import ru.relicarium.pledge.domain.model.Pledge;

import java.util.UUID;

public interface PledgeStatusService {

    Pledge changeStatus(UUID pledgeId, PledgeStatusTransition statusTransition);
}
