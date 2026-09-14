package ru.relicarium.ledger.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.relicarium.ledger.domain.enums.DocumentType;
import ru.relicarium.ledger.domain.model.JournalDocument;

import java.util.UUID;

public interface JournalDocumentRepository extends JpaRepository<JournalDocument, UUID> {

    boolean existsByDocumentTypeAndPledgeIdAndOperationId(
            DocumentType documentType, UUID pledgeId, String operationId);
}
