package ru.relicarium.pledge.application.dto;

import jakarta.validation.constraints.NotBlank;

public record SendToAuctionPledgeCommand(
        @NotBlank
        String operationId
) {
}
