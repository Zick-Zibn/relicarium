package ru.relicarium.pledge.domain.state;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.relicarium.pledge.domain.enums.PledgeStatus;
import ru.relicarium.pledge.domain.enums.PledgeStatusTransition;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тестирование действия при смене статуса")
public class PledgeStateMachineTest {

    private final PledgeStateMachine pledgeStateMachine = new PledgeStateMachine();

    @ParameterizedTest(name = "Из статуса {0} при действии {1} должен получиться статус {2}")
    @CsvSource({
            "ACCEPTED, ACTIVATE,        ACTIVE",
            "ACTIVE,   ENTER_GRACE,     GRACE",
            "ACTIVE,   REDEEM,          REDEEMED",
            "GRACE,    MARK_FOR_SALE,   FOR_SALE",
            "GRACE,    REDEEM,          REDEEMED",
            "FOR_SALE, SEND_TO_AUCTION, ON_AUCTION",
            "ON_AUCTION, MARK_SOLD,     SOLD",
            "ON_AUCTION, RETURN_UNSOLD, FOR_SALE"
    })
    void shouldTransitionSuccessfully(PledgeStatus current, PledgeStatusTransition action, PledgeStatus expected) {
        // When
        PledgeStatus result = pledgeStateMachine.transition(current, action);

        // Then
        assertEquals(expected, result, "Переход выполнен неверно или вернулся не тот статус");
    }

    @Test
    @DisplayName("Должно выбросить IllegalArgumentException, если текущий статус равен null")
    void shouldThrowExceptionWhenCurrentStatusIsNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pledgeStateMachine.transition(null, PledgeStatusTransition.ACTIVATE)
        );

        assertEquals("Не указан статус (current)", exception.getMessage());
    }

    @ParameterizedTest(name = "Из {0} при {1} переход должен быть запрещён")
    @CsvSource({
            "REDEEMED,   ACTIVATE",
            "SOLD,       RETURN_UNSOLD"
    })
    void shouldRejectTransition(PledgeStatus current, PledgeStatusTransition action) {
        assertThrows(IllegalStateException.class,
                () -> pledgeStateMachine.transition(current, action));
    }

    @Test
    @DisplayName("Должно выбросить IllegalArgumentException, если действие равно null")
    void shouldThrowExceptionWhenActionIsNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pledgeStateMachine.transition(PledgeStatus.ACCEPTED, null)
        );

        assertEquals("Не указано действие (action)", exception.getMessage());
    }

    @Test
    @DisplayName("Должно выбросить IllegalStateException при попытке неверного перехода (например, из ACCEPTED вызвать REDEEM)")
    void shouldThrowExceptionWhenTransitionIsInvalid() {
        // Given
        PledgeStatus current = PledgeStatus.ACCEPTED;
        PledgeStatusTransition invalidAction = PledgeStatusTransition.REDEEM;

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                pledgeStateMachine.transition(current, invalidAction)
        );

        assertTrue(exception.getMessage().contains(current.name()), "Текст ошибки должен содержать текущий статус");
        assertTrue(exception.getMessage().contains(invalidAction.name()), "Текст ошибки должен содержать название действия");
    }
}
