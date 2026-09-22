package ru.relicarium.auction.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.relicarium.auction.domain.model.Lot;

import java.util.Optional;
import java.util.UUID;

public interface LotRepository extends JpaRepository<Lot, UUID> {

    Optional<Lot> findByPledgeId(UUID pledgeId);
    Optional<Lot> findByPledgeIdAndOperationId(UUID pledgeId, String operationId);
    boolean existsByPledgeIdAndOperationId(UUID pledgeId, String operationId);
}
