package ru.relicarium.pledge.domain.enums;

public enum PledgeStatusTransition {

    ACTIVATE,           // деньги выданы → залог активен
    ENTER_GRACE,        // срок вышел, льготный период
    MARK_FOR_SALE,      // готов к торгам
    SEND_TO_AUCTION,    // ушёл на торги
    MARK_SOLD,          // продан
    RETURN_UNSOLD,      // с торгов не продали
    REDEEM              // выкуплен
}
