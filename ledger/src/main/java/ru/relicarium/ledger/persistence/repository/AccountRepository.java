package ru.relicarium.ledger.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.relicarium.ledger.domain.model.Account;

public interface AccountRepository extends JpaRepository<Account, String> {
}
