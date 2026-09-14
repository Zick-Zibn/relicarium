package ru.relicarium.ledger.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
public class Account {

    @Id
    @Column(length = 30, nullable = false)
    String code;

    @Column(name = "name", length = 255, nullable = false)
    @NotNull
    String name;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    @NotNull
    OffsetDateTime createdAt;
}
