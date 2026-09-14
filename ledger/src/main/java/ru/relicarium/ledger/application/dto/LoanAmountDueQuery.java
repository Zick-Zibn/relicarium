package ru.relicarium.ledger.application.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record LoanAmountDueQuery(
        @NotNull
        UUID pledgeId,
        LocalDate asOf
) {
}
