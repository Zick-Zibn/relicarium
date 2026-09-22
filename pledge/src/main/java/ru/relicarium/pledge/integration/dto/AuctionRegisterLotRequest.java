package ru.relicarium.pledge.integration.dto;

import java.util.UUID;

public record AuctionRegisterLotRequest(
        UUID pledgeI,
        String operationId
) {
}
