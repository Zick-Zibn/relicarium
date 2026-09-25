package ru.relicarium.pledge.application.dto.response;

import jakarta.annotation.Nullable;
import ru.relicarium.pledge.domain.enums.PledgeStatus;
import ru.relicarium.pledge.domain.enums.StorageStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record PledgeResponse(
        UUID id,
        PledgeStatus status,
        BigDecimal loanAmount,
        BigDecimal interestRate,
        int termDays,
        OffsetDateTime acceptedAt,
        LocalDate dueDate,
        UUID clientId,
        UUID itemId,
        String itemName,
        StorageStatus itemStorageStatus,
        @Nullable
        OffsetDateTime redeemedAt
) {
}
