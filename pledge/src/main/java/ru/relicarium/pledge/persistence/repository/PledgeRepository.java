package ru.relicarium.pledge.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.relicarium.pledge.domain.enums.PledgeStatus;
import ru.relicarium.pledge.domain.model.Pledge;

import java.util.List;
import java.util.UUID;

public interface PledgeRepository extends JpaRepository<Pledge, UUID> {

    List<Pledge> findByClient_Id(UUID clientId);

    List<Pledge> findByStatus(PledgeStatus status);
}
