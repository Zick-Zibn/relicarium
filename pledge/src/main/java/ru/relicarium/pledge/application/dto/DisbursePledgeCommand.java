package ru.relicarium.pledge.application.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.OffsetDateTime;

public record DisbursePledgeCommand(
        @NotBlank
        String operationId,
        OffsetDateTime openedAt
) {
}
