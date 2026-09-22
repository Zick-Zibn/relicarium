package ru.relicarium.pledge.application.service;

import ru.relicarium.pledge.application.dto.SendToAuctionPledgeCommand;
import ru.relicarium.pledge.domain.model.Pledge;

import java.util.UUID;

public interface PledgeAuctionSubmissionService {

    Pledge submit(UUID pledgeIв, SendToAuctionPledgeCommand command);
}
