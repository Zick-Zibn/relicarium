package ru.relicarium.ledger.application.service;

import ru.relicarium.ledger.application.dto.PayInterestCommand;
import ru.relicarium.ledger.application.dto.RepayLoanCommand;
import ru.relicarium.ledger.domain.model.Loan;

public interface LoanCashService {

    Loan payInterest(PayInterestCommand payInterestCommand);

    Loan repay(RepayLoanCommand repayLoanCommand);
}
