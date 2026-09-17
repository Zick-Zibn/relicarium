package ru.relicarium.pledge.application.service;

import ru.relicarium.pledge.application.dto.DisbursePledgeCommand;
import ru.relicarium.pledge.domain.model.Pledge;

import java.util.UUID;

public interface PledgeDisbursementService {

    Pledge disburse(UUID pledgeId, DisbursePledgeCommand command);
}
