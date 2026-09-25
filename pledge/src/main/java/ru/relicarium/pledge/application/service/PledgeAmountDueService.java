package ru.relicarium.pledge.application.service;

import ru.relicarium.pledge.application.dto.response.PledgeAmountDueResponse;

import java.time.LocalDate;
import java.util.UUID;

public interface PledgeAmountDueService {

    PledgeAmountDueResponse getAmountDue(UUID pledgeId, LocalDate asOf);
}
