package ru.relicarium.pledge.application.service;

import org.springframework.data.domain.Pageable;
import ru.relicarium.pledge.application.dto.response.PledgePageResponse;
import ru.relicarium.pledge.domain.enums.PledgeStatus;
import ru.relicarium.pledge.domain.model.Pledge;

import java.util.UUID;

public interface PledgeQueryService {

    Pledge getById(UUID pledgeId);
    PledgePageResponse listPledges(String phone, PledgeStatus status, Pageable pageable);

}
