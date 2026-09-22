package ru.relicarium.auction.application.dto.responce;

import ru.relicarium.auction.domain.enums.LotStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record LotResponse(
        UUID id,
        UUID pledgeId,
        LotStatus status,
        OffsetDateTime createdAt
) {
}
