package ru.relicarium.ledger.application.service;

import ru.relicarium.ledger.application.dto.LoanAmountDueQuery;
import ru.relicarium.ledger.application.dto.LoanAmountDueResult;

public interface LoanAmountDueService {

    LoanAmountDueResult getAmountDue(LoanAmountDueQuery loanAmountDueQuery);
}
