package ru.relicarium.pledge.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SendToAuctionPledgeRequest(
        @NotBlank
        String operationId
) {
}
