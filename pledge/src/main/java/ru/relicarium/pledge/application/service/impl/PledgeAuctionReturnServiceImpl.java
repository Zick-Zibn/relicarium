package ru.relicarium.pledge.application.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.relicarium.pledge.application.dto.AuctionReturnPledgeCommand;
import ru.relicarium.pledge.application.service.PledgeAuctionReturnService;
import ru.relicarium.pledge.application.service.PledgeStatusService;
import ru.relicarium.pledge.domain.enums.PledgeStatus;
import ru.relicarium.pledge.domain.enums.PledgeStatusTransition;
import ru.relicarium.pledge.domain.model.Pledge;
import ru.relicarium.pledge.integration.AuctionClient;
import ru.relicarium.pledge.integration.dto.AuctionCompleteLotRequest;
import ru.relicarium.pledge.integration.dto.AuctionLotResponse;
import ru.relicarium.pledge.persistence.repository.PledgeRepository;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PledgeAuctionReturnServiceImpl implements PledgeAuctionReturnService {

    private final PledgeRepository pledgeRepository;
    private final AuctionClient auctionClient;
    private final PledgeStatusService pledgeStatusService;

    @Override
    @Transactional
    public Pledge returnLot(UUID pledgeId, AuctionReturnPledgeCommand command) {

        Pledge pledge = pledgeRepository.findById(pledgeId).orElseThrow(() ->
                new EntityNotFoundException("Pledge not found " + pledgeId));

        if (pledge.getStatus() != PledgeStatus.ON_AUCTION) {
            throw new IllegalStateException("Auction return allowed only for ON_AUCTION");
        }

        AuctionCompleteLotRequest lotRequest = new AuctionCompleteLotRequest(
                pledge.getId(),
                command.operationId(),
                "UNSOLD",
                BigDecimal.ZERO
        );
        AuctionLotResponse lotResponse = auctionClient.completeLot(lotRequest);

        if (!"UNSOLD".equals(lotResponse.status())) {
            throw new IllegalStateException("Expected UNSOLD lot status from auction");
        }
        pledge = pledgeStatusService.changeStatus(pledgeId, PledgeStatusTransition.RETURN_UNSOLD);

        return pledge;
    }
}
