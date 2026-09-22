package ru.relicarium.auction.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.relicarium.auction.domain.enums.LotStatus;

import java.util.UUID;

public record CompleteLotCommand(
        UUID pledgeId,
        @NotBlank
        String operationId,
        @NotNull
        LotStatus finalStatus
) {
}
