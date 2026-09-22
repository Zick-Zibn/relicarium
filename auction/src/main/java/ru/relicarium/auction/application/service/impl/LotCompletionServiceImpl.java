package ru.relicarium.auction.application.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.relicarium.auction.application.dto.CompleteLotCommand;
import ru.relicarium.auction.application.service.LotCompletionService;
import ru.relicarium.auction.domain.enums.LotStatus;
import ru.relicarium.auction.domain.model.Lot;
import ru.relicarium.auction.persistence.repository.LotRepository;

@Service
@RequiredArgsConstructor
public class LotCompletionServiceImpl implements LotCompletionService {

    private final LotRepository lotRepository;

    @Override
    @Transactional
    public Lot complete(CompleteLotCommand command) {

        Lot lot = lotRepository.findByPledgeId(command.pledgeId()).orElseThrow(() ->
                new EntityNotFoundException("Lot does,t exist by pledgeId: " + command.pledgeId()));

        if (command.finalStatus() != LotStatus.SOLD && command.finalStatus() != LotStatus.UNSOLD) {
            throw new IllegalStateException("Lot status must be SOLD or UNSOLD");
        }
        if (lot.getCompletionOperationId() != null && lot.getCompletionOperationId().equals(command.operationId())) {
            return lot;
        }

        if (lot.getStatus() != LotStatus.OPEN) {
            throw new IllegalStateException("Lot must be status OPEN");
        }
        lot.setStatus(command.finalStatus());
        lot.setCompletionOperationId(command.operationId());
        lotRepository.save(lot);

        return lot;
    }
}
