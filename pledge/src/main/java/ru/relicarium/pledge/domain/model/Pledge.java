package ru.relicarium.pledge.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ru.relicarium.pledge.domain.enums.PledgeStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "pledges")
@Getter
@Setter
@NoArgsConstructor
public class Pledge {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JoinColumn(name = "item_id", unique = true, nullable = false)
    @ManyToOne(optional = false)
    private Item item;

    @JoinColumn(name = "client_id", nullable = false)
    @ManyToOne(optional = false)
    private Client client;

    @Column(name = "loan_amount", precision = 15, scale = 2, nullable = false)
    @Positive
    @NotNull
    private BigDecimal loanAmount;

    @Column(name = "interest_rate", precision = 5, scale = 4, nullable = false)
    @PositiveOrZero
    @NotNull
    private BigDecimal interestRate;

    @Column(name = "term_days", nullable = false)
    @Positive
    @NotNull
    private Integer termDays;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    private PledgeStatus status = PledgeStatus.ACCEPTED;

    @CreationTimestamp
    @Column(name = "accepted_at", nullable = false)
    private OffsetDateTime acceptedAt;

    @Column(name = "due_date", nullable = false)
    @NotNull
    private LocalDate dueDate;

    @Column(name = "redeemed_at")
    private OffsetDateTime redeemedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
