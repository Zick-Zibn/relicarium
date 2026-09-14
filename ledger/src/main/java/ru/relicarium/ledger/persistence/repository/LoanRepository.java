package ru.relicarium.ledger.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.relicarium.ledger.domain.model.Loan;

import java.util.Optional;
import java.util.UUID;

public interface LoanRepository extends JpaRepository<Loan, UUID> {

    Optional<Loan> findByPledgeId(UUID pledgeId);
}
