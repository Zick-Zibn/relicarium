package ru.relicarium.pledge.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.relicarium.pledge.domain.model.AppUser;

import java.util.Optional;
import java.util.UUID;

public interface AppUserRepository extends JpaRepository<AppUser, UUID> {

    Optional<AppUser> findByUsername(String username);
}
