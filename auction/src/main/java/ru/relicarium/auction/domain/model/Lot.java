package ru.relicarium.auction.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ru.relicarium.auction.domain.enums.LotStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "lots")
@Getter
@Setter
@NoArgsConstructor
public class Lot {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(name = "pledge_id")
    UUID pledgeId;

    @Column(name = "operation_id", length = 100, nullable = false)
    String operationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    LotStatus status;

    @CreationTimestamp
    @Column(name = "created_at")
    OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    OffsetDateTime updateAt;

    @Column(name = "completion_operation_id", length = 100)
    private String completionOperationId;

    @Column(name = "sale_price", precision = 15, scale = 2)
    private BigDecimal salesPrice;
}
