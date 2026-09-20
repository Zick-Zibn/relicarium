package ru.relicarium.pledge.application.dto.request;

import jakarta.validation.constraints.NotNull;
import ru.relicarium.pledge.domain.enums.PledgeStatusTransition;

public record ApplyPledgeTransitionRequest(
        @NotNull
        PledgeStatusTransition transition
) {
}
