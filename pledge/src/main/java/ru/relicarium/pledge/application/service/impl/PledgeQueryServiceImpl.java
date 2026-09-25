package ru.relicarium.pledge.application.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.relicarium.pledge.api.mapper.PledgeApiMapper;
import ru.relicarium.pledge.application.dto.response.PledgePageResponse;
import ru.relicarium.pledge.application.service.PledgeQueryService;
import ru.relicarium.pledge.domain.enums.PledgeStatus;
import ru.relicarium.pledge.domain.model.Pledge;
import ru.relicarium.pledge.persistence.repository.ClientRepository;
import ru.relicarium.pledge.persistence.repository.PledgeRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PledgeQueryServiceImpl implements PledgeQueryService {

    private final PledgeRepository pledgeRepository;
    private final ClientRepository clientRepository;
    private final PledgeApiMapper pledgeApiMapper;

    @Override
    public Pledge getById(UUID pledgeId) {

        return pledgeRepository.findById(pledgeId).orElseThrow(() ->
                new EntityNotFoundException("Pledge not found: " + pledgeId));

    }

    @Override
    @Transactional(readOnly = true)
    public PledgePageResponse listPledges(String phone, PledgeStatus status, Pageable pageable) {

        if (phone == null && status == null) {
            throw new IllegalArgumentException("At least one of phone or status must be provided");
        }
        if (pageable.getPageSize() > 100) {
            throw new IllegalArgumentException("Page size must not exceed 100");
        }

        Page<Pledge> page;

        if (phone != null) {
            var client = clientRepository.findByPhone(phone);
            if (client.isEmpty()) {
                return pledgeApiMapper.toPageResponse(Page.empty(pageable));
            }
            UUID clientId = client.get().getId();
            if (status == null) {
                page = pledgeRepository.findByClient_Id(clientId, pageable);
            } else {
                page = pledgeRepository.findByClient_IdAndStatus(clientId, status, pageable);
            }
        } else {
            page = pledgeRepository.findByStatus(status, pageable);
        }

        return pledgeApiMapper.toPageResponse(page);
    }
}
