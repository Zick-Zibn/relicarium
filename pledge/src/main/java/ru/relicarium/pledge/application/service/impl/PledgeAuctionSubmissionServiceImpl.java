package ru.relicarium.pledge.application.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.relicarium.pledge.application.dto.SendToAuctionPledgeCommand;
import ru.relicarium.pledge.application.service.PledgeAuctionSubmissionService;
import ru.relicarium.pledge.application.service.PledgeStatusService;
import ru.relicarium.pledge.domain.enums.PledgeStatus;
import ru.relicarium.pledge.domain.enums.PledgeStatusTransition;
import ru.relicarium.pledge.domain.model.Pledge;
import ru.relicarium.pledge.integration.AuctionClient;
import ru.relicarium.pledge.integration.dto.AuctionLotResponse;
import ru.relicarium.pledge.integration.dto.AuctionRegisterLotRequest;
import ru.relicarium.pledge.persistence.repository.PledgeRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PledgeAuctionSubmissionServiceImpl implements PledgeAuctionSubmissionService {

    private final PledgeRepository pledgeRepository;
    private final AuctionClient auctionClient;
    private final PledgeStatusService pledgeStatusService;


    @Override
    @Transactional
    public Pledge submit(UUID pledgeId, SendToAuctionPledgeCommand command) {

        Pledge pledge = pledgeRepository.findById(pledgeId).orElseThrow(() ->
                new EntityNotFoundException("Pledge not found " + pledgeId));

        if (pledge.getStatus() != PledgeStatus.FOR_SALE) {
            throw new IllegalStateException("Auction submission allowed only for FOR_SALE");
        }

        AuctionRegisterLotRequest lotRequest = new AuctionRegisterLotRequest(
                pledge.getId(),
                command.operationId()
        );

        AuctionLotResponse lotResponse = auctionClient.registerLot(lotRequest);

        if (!"OPEN".equals(lotResponse.status())) {
            throw new IllegalStateException("The lot must be in status OPEN");
        }
        pledge = pledgeStatusService.changeStatus(pledge.getId(), PledgeStatusTransition.SEND_TO_AUCTION);

        return pledge;
    }
}
