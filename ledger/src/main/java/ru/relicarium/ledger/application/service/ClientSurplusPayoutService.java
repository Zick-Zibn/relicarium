package ru.relicarium.ledger.application.service;

import ru.relicarium.ledger.application.dto.PayClientSurplusCommand;
import ru.relicarium.ledger.domain.model.Loan;

public interface ClientSurplusPayoutService {

    Loan payClientSurplus(PayClientSurplusCommand command);
}
