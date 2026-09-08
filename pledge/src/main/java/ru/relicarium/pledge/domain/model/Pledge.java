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
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "pledges")
@Getter
@Setter
@NoArgsConstructor
public class Plege {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JoinColumn(name = "item_id")
    @ManyToOne
    private Item item;

    @JoinColumn(name = "client_id")
    @ManyToOne
    private Client client;

    @Column(name = "loan_amount", nullable = false)
    @Positive
    @NotNull
    private BigDecimal loanAmount;

    @Column(name = "interest_rate")
    @PositiveOrZero
    @NotNull
    private BigDecimal interestRate;

    @Column(name = "term_days")
    @Positive
    @NotNull
    private Integer termDays;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private PledgeStatus status = PledgeStatus.ACCEPTED;

    @Column(name = "accepted_at")
    @CreationTimestamp
    private OffsetDateTime acceptedAt;

    @Column(name = "due_date")
    @NotNull
    private Date dueDate;

    @Column(name = "redeemed_at")
    private OffsetDateTime redeemedAt;

    @CreationTimestamp
    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}
