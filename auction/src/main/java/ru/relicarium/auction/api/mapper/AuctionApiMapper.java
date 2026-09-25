package ru.relicarium.auction.api.mapper;

import org.springframework.stereotype.Component;
import ru.relicarium.auction.application.dto.CompleteLotCommand;
import ru.relicarium.auction.application.dto.CreateLotCommand;
import ru.relicarium.auction.application.dto.request.CompleteLotRequest;
import ru.relicarium.auction.application.dto.request.CreateLotRequest;
import ru.relicarium.auction.application.dto.response.LotResponse;
import ru.relicarium.auction.domain.model.Lot;

@Component
public class AuctionApiMapper {

    public CreateLotCommand toCreateLotCommand(CreateLotRequest request) {
        return new CreateLotCommand(
                request.pledgeId(),
                request.operationId()
        );
    }

    public LotResponse toLotResponse(Lot lot) {
        return new LotResponse(
                lot.getId(),
                lot.getPledgeId(),
                lot.getStatus(),
                lot.getCreatedAt(),
                lot.getSalesPrice()
        );
    }

    public CompleteLotCommand toCompleteLotCommand(CompleteLotRequest request) {
        return new CompleteLotCommand(
                request.pledgeId(),
                request.operationId(),
                request.finalStatus(),
                request.saleProceeds()
        );
    }
}
