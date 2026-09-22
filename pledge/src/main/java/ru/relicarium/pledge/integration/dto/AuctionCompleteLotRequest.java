package ru.relicarium.pledge.integration.dto;

import java.util.UUID;

public record AuctionCompleteLotRequest(
        UUID pledgeId,
        String operationId,
        String finalStatus
) {
}
