package ru.relicarium.pledge.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CreatePledgeRequest(
        @NotBlank
        String fullName,
        @NotBlank
        String phone,
        String passportSeries,
        String passportNumber,
        @NotBlank
        String itemName,
        String description,
        @NotBlank
        String category,
        @Positive
        BigDecimal estimatedValue,
        @Positive
        BigDecimal loanAmount,
        @PositiveOrZero
        BigDecimal interestRate,
        @Positive
        int termDays
) {
}
