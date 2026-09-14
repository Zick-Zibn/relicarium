package ru.relicarium.ledger.application.service;

import ru.relicarium.ledger.application.dto.DisburseLoanCommand;
import ru.relicarium.ledger.domain.model.Loan;

public interface LoanDisbursementService {

    Loan disburse(DisburseLoanCommand disburseLoanCommand);
}
