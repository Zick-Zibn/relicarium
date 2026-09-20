package ru.relicarium.pledge.application.dto;

import jakarta.validation.constraints.NotNull;
import ru.relicarium.pledge.domain.enums.PledgeStatusTransition;

public record ApplyPledgeTransitionCommand(
        @NotNull
        PledgeStatusTransition transition
) {
}
