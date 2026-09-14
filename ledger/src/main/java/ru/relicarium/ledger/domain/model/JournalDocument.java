package ru.relicarium.ledger.domain.model;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import ru.relicarium.ledger.domain.enums.DocumentType;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "journal_documents")
@Getter
@Setter
@NoArgsConstructor
public class JournalDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", length = 30, nullable = false)
    DocumentType documentType;

    @Column(name = "pledge_id", nullable = false)
    UUID pledgeId;

    @ManyToOne
    @JoinColumn(name = "loan_id")
    Loan loan;

    @Column(name = "operation_id", length = 100, nullable = false)
    String operationId;

    @Column(name = "posted_at", nullable = false)
    @CreationTimestamp
    OffsetDateTime postedAt;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    OffsetDateTime createdAt;
}
