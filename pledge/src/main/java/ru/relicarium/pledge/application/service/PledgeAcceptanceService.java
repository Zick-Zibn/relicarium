package ru.relicarium.pledge.application.service;

import ru.relicarium.pledge.application.dto.AcceptPledgeCommand;
import ru.relicarium.pledge.domain.model.Pledge;

public interface PledgeAcceptanceService {

    Pledge acceptPledge(AcceptPledgeCommand acceptPledgeCommand);
}
