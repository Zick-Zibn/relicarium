package ru.relicarium.pledge.integration;

import ru.relicarium.pledge.integration.dto.AuctionCompleteLotRequest;
import ru.relicarium.pledge.integration.dto.AuctionLotResponse;
import ru.relicarium.pledge.integration.dto.AuctionRegisterLotRequest;

public interface AuctionClient {

    AuctionLotResponse registerLot(AuctionRegisterLotRequest request);
    AuctionLotResponse completeLot(AuctionCompleteLotRequest request);
}
