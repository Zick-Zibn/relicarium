package ru.relicarium.pledge.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.relicarium.pledge.domain.model.Item;

import java.util.UUID;

public interface ItemRepository extends JpaRepository<Item, UUID> {
}
