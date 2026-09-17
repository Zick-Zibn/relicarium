package ru.relicarium.pledge.application.service;

import ru.relicarium.pledge.application.dto.PayInterestPledgeCommand;
import ru.relicarium.pledge.domain.model.Pledge;

import java.util.UUID;

public interface PledgeInterestPaymentService {

    Pledge payInterest(UUID pledgeId, PayInterestPledgeCommand command);
}
