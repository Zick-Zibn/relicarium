package ru.relicarium.pledge.application.service;

import ru.relicarium.pledge.application.dto.RedeemPledgeCommand;
import ru.relicarium.pledge.domain.model.Pledge;

import java.util.UUID;

public interface PledgeRedemptionService {

    Pledge redeem(UUID pledgeId, RedeemPledgeCommand command);
}
