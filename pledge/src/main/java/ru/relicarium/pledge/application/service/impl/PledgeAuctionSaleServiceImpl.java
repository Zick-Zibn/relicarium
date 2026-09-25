package ru.relicarium.pledge.application.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.relicarium.pledge.application.dto.AuctionSalePledgeCommand;
import ru.relicarium.pledge.application.service.PledgeAuctionSaleService;
import ru.relicarium.pledge.application.service.PledgeStatusService;
import ru.relicarium.pledge.domain.enums.PledgeStatus;
import ru.relicarium.pledge.domain.enums.PledgeStatusTransition;
import ru.relicarium.pledge.domain.model.Pledge;
import ru.relicarium.pledge.integration.AuctionClient;
import ru.relicarium.pledge.integration.LedgerClient;
import ru.relicarium.pledge.integration.dto.AuctionCompleteLotRequest;
import ru.relicarium.pledge.integration.dto.AuctionLotResponse;
import ru.relicarium.pledge.integration.dto.LedgerLoanResponse;
import ru.relicarium.pledge.integration.dto.LedgerSettleAfterAuctionRequest;
import ru.relicarium.pledge.persistence.repository.PledgeRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PledgeAuctionSaleServiceImpl implements PledgeAuctionSaleService {

    private final PledgeRepository pledgeRepository;
    private final AuctionClient auctionClient;
    private final PledgeStatusService pledgeStatusService;
    private final LedgerClient ledgerClient;

    @Override
    @Transactional
    public Pledge saleLot(UUID pledgeId, AuctionSalePledgeCommand command) {

        Pledge pledge = pledgeRepository.findById(pledgeId).orElseThrow(() ->
                new EntityNotFoundException("Pledge not found " + pledgeId));

        if (pledge.getStatus() != PledgeStatus.ON_AUCTION) {
            throw new IllegalStateException("Auction sale allowed only for ON_AUCTION");
        }
        AuctionCompleteLotRequest lotRequest = new AuctionCompleteLotRequest(
                pledge.getId(),
                command.operationId(),
                "SOLD",
                command.saleProceeds()
        );
        AuctionLotResponse lotResponse = auctionClient.completeLot(lotRequest);

        if (!"SOLD".equals(lotResponse.status())) {
            throw new IllegalStateException("Expected SOLD lot status from auction");
        }
        LedgerSettleAfterAuctionRequest ledgerSettleAfterAuctionRequest = new LedgerSettleAfterAuctionRequest(
                pledge.getId(),
                command.saleProceeds(),
                command.operationId(),
                null
        );

        LedgerLoanResponse ledgerLoanResponse = ledgerClient.settleAfterAuction(ledgerSettleAfterAuctionRequest);
        if (ledgerLoanResponse.closedAt() != null) {
            pledge = pledgeStatusService.changeStatus(pledgeId, PledgeStatusTransition.MARK_SOLD);
        } else {
            throw new IllegalStateException("closedAt is null");
        }

        return pledge;
    }
}
