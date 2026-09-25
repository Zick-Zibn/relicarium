package ru.relicarium.pledge.application.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.relicarium.pledge.api.mapper.PledgeApiMapper;
import ru.relicarium.pledge.application.dto.response.PledgeAmountDueResponse;
import ru.relicarium.pledge.application.service.PledgeAmountDueService;
import ru.relicarium.pledge.domain.model.Pledge;
import ru.relicarium.pledge.integration.LedgerClient;
import ru.relicarium.pledge.integration.dto.LedgerAmountDueResponse;
import ru.relicarium.pledge.persistence.repository.PledgeRepository;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PledgeAmountDueServiceImpl implements PledgeAmountDueService {

    private final PledgeRepository pledgeRepository;
    private final LedgerClient ledgerClient;
    private final PledgeApiMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public PledgeAmountDueResponse getAmountDue(UUID pledgeId, LocalDate asOf) {

        Pledge pledge = pledgeRepository.findById(pledgeId).orElseThrow(() ->
                new EntityNotFoundException("Pledge not found: " + pledgeId));

        LedgerAmountDueResponse amountDueResponse = ledgerClient.getAmount(pledge.getId(), asOf);

        return mapper.toAmountDueResponse(amountDueResponse);
    }
}
