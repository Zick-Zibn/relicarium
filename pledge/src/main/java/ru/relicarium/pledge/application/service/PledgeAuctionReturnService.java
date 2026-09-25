package ru.relicarium.pledge.application.service;

import ru.relicarium.pledge.application.dto.AuctionReturnPledgeCommand;
import ru.relicarium.pledge.domain.model.Pledge;

import java.util.UUID;

public interface PledgeAuctionReturnService {

    Pledge returnLot(UUID pledgeId, AuctionReturnPledgeCommand command);
}
