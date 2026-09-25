package ru.relicarium.pledge.application.service;

import ru.relicarium.pledge.application.dto.AuctionSalePledgeCommand;
import ru.relicarium.pledge.domain.model.Pledge;

import java.util.UUID;

public interface PledgeAuctionSaleService {

    Pledge saleLot(UUID pledgeId, AuctionSalePledgeCommand command);
}
