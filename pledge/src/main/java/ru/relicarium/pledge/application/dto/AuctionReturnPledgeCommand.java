package ru.relicarium.pledge.application.dto;

import jakarta.validation.constraints.NotBlank;

public record AuctionReturnPledgeCommand(@NotBlank String operationId) {
}
