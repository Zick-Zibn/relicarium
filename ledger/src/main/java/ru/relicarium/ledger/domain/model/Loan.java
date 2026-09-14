package ru.relicarium.ledger.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "loans")
@Getter
@Setter
@NoArgsConstructor
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(name = "pledge_id", nullable = false, unique = true)
    @NotNull
    UUID pledgeId;

    @Column(name = "client_id", nullable = false)
    @NotNull
    UUID    clientId;

    @Column(name = "principal", nullable = false, precision = 15, scale = 2)
    @NotNull
    @Positive
    BigDecimal principal;

    @Column(name = "interest_rate", nullable = false, precision = 5, scale = 4)
    @NotNull
    @PositiveOrZero
    BigDecimal interestRate;

    @Column(name = "opened_at", nullable = false)
    @NotNull
    OffsetDateTime openedAt;

    @Column(name = "due_date", nullable = false)
    @NotNull
    LocalDate dueDate;

    @Column(name = "closed_at")
    OffsetDateTime closedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    OffsetDateTime updatedAt;
}
