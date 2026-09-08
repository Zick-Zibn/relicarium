package ru.relicarium.pledge.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

@Valid
public record AcceptPledgeCommand(
        @NotBlank(message = "ФИО должно быть указано")
        String fullName,
        @NotBlank(message = "Телефон должен быть указан ")
        String phone,
        String passportSeries,
        String passportNumber,
        @NotBlank(message = "Наименование вещи должно быть указано")
        String itemName,
        String description,
        @NotBlank(message = "Категория должна быть указано")
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
