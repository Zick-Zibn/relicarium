package ru.relicarium.pledge.application.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.time.OffsetDateTime;

public record DisbursePledgeRequest(
        @NotBlank
        String operationId,
        OffsetDateTime openedAt
) {
}
