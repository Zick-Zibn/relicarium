package ru.relicarium.ledger.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.relicarium.ledger.domain.model.JournalLine;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface JournalLineRepository extends JpaRepository<JournalLine, UUID> {

    List<JournalLine> findByJournalDocument_Loan_Id(UUID loanId);

    @Query("""
            SELECT SUM(jl.debit) - SUM(jl.credit)
            FROM JournalLine jl\s
            where jl.journalDocument.loan.id = :loanId
                AND jl.account.code = 'LOANS'
           """)
    BigDecimal sumOutstandingPrincipal(@Param("loanId") UUID loanId);

    @Query("""
        SELECT COALESCE(SUM(jl.credit), 0) - COALESCE(SUM(jl.debit), 0)
        FROM JournalLine jl
        WHERE jl.journalDocument.loan.id = :loanId
            AND jl.account.code = 'INTEREST_INCOME'
        """)
    BigDecimal sumRecognizedInterestIncome(@Param("loanId") UUID loanId);

    @Query("""
        SELECT COALESCE(SUM(jl.credit), 0) - COALESCE(SUM(jl.debit), 0)
        FROM JournalLine jl
        WHERE jl.account.code = 'CLIENT_PAYABLE'
            AND jl.journalDocument.pledgeId = :pledgeId
        """)
    BigDecimal sumClientPayableBalanceByPledgeId(@Param("pledgeId") UUID pledgeId);
}
