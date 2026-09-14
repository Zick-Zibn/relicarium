package ru.relicarium.ledger.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "journal_lines")
@Getter
@Setter
@NoArgsConstructor
public class JournalLine {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "document_id", nullable = false)
    JournalDocument journalDocument;

    @ManyToOne(optional = false)
    @JoinColumn(name = "account_code", nullable = false)
    Account account;

    @Column(name = "debit", nullable = false, precision = 15, scale = 2)
    @PositiveOrZero
    BigDecimal debit;

    @Column(name = "credit", nullable = false, precision = 15, scale = 2)
    @PositiveOrZero
    BigDecimal credit;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    OffsetDateTime createdAt;
}
