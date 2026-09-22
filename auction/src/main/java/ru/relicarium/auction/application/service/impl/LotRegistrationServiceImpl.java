package ru.relicarium.auction.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.relicarium.auction.application.dto.CreateLotCommand;
import ru.relicarium.auction.application.service.LotRegistrationService;
import ru.relicarium.auction.domain.enums.LotStatus;
import ru.relicarium.auction.domain.model.Lot;
import ru.relicarium.auction.persistence.repository.LotRepository;

@Service
@RequiredArgsConstructor
public class LotRegistrationServiceImpl implements LotRegistrationService {

    private final LotRepository lotRepository;

    @Override
    @Transactional
    public Lot register(CreateLotCommand command) {

        if (lotRepository.existsByPledgeIdAndOperationId(command.pledgeId(), command.operationId())) {
            return lotRepository.findByPledgeIdAndOperationId(command.pledgeId(), command.operationId())
                    .orElseThrow(() -> new IllegalStateException("Lot inconsistency"));
        }

        if (lotRepository.findByPledgeId(command.pledgeId()).isPresent()) {
            throw new IllegalStateException("Lot already exists for pledge: " + command.pledgeId());
        }

        Lot lot = new Lot();
        lot.setPledgeId(command.pledgeId());
        lot.setOperationId(command.operationId());
        lot.setStatus(LotStatus.OPEN);
        lot = lotRepository.save(lot);

        return lot;
    }
}
