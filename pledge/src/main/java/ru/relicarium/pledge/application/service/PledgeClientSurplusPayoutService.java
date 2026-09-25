package ru.relicarium.pledge.application.service;

import ru.relicarium.pledge.application.dto.PayClientSurplusPledgeCommand;
import ru.relicarium.pledge.domain.model.Pledge;

import java.util.UUID;

public interface PledgeClientSurplusPayoutService {

    Pledge payClientSurplus(UUID pledgeId, PayClientSurplusPledgeCommand command);
}
