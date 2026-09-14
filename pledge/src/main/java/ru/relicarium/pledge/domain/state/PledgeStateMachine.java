package ru.relicarium.pledge.domain.state;

import ru.relicarium.pledge.domain.enums.PledgeStatus;
import ru.relicarium.pledge.domain.enums.PledgeStatusTransition;

public class PledgeStateMachine {

    public PledgeStatus transition(PledgeStatus current, PledgeStatusTransition action) {

        if (current == null) {
            throw new IllegalArgumentException("Не указан статус (current)");
        }
        if (action == null) {
            throw new IllegalArgumentException("Не указано действие (action)");
        }

        switch (current) {
            case ACCEPTED:
                switch (action) {
                    case ACTIVATE:
                        return PledgeStatus.ACTIVE;
                    default:
                        throw new IllegalStateException(this.getErrorMessage(current, action));
                }
            case ACTIVE:
                switch (action) {
                    case ENTER_GRACE:
                        return PledgeStatus.GRACE;
                    case REDEEM:
                        return PledgeStatus.REDEEMED;
                    default:
                        throw new IllegalStateException(this.getErrorMessage(current, action));
                }
            case GRACE:
                switch (action) {
                    case MARK_FOR_SALE:
                        return PledgeStatus.FOR_SALE;
                    case REDEEM:
                        return PledgeStatus.REDEEMED;
                    default:
                        throw new IllegalStateException(this.getErrorMessage(current, action));
                }
            case FOR_SALE:
                switch (action) {
                    case SEND_TO_AUCTION:
                        return PledgeStatus.ON_AUCTION;
                    default:
                        throw new IllegalStateException(this.getErrorMessage(current, action));
                }
            case ON_AUCTION:
                switch (action) {
                    case MARK_SOLD:
                        return PledgeStatus.SOLD;
                    case RETURN_UNSOLD:
                        return PledgeStatus.FOR_SALE;
                    default:
                        throw new IllegalStateException(getErrorMessage(current, action));
                }
            default:
                throw new IllegalStateException(getErrorMessage(current, action));
        }
    }

    private String getErrorMessage(PledgeStatus current, PledgeStatusTransition action) {

        return String.format("Action %s is not allowed from status %s", current, action);
    }
}
