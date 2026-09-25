package ru.relicarium.pledge.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.relicarium.pledge.domain.enums.PledgeStatus;
import ru.relicarium.pledge.domain.model.Pledge;

import java.util.UUID;

public interface PledgeRepository extends JpaRepository<Pledge, UUID> {

    Page<Pledge> findByClient_Id(UUID clientId, Pageable pageable);
    Page<Pledge> findByStatus(PledgeStatus status, Pageable pageable);
    Page<Pledge> findByClient_IdAndStatus(UUID clientId, PledgeStatus status, Pageable pageable);
}
