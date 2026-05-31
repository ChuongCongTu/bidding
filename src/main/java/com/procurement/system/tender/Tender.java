package com.procurement.system.tender;

import com.procurement.system.plan.Plan;
import com.procurement.system.tender.enums.TenderMethod;
import com.procurement.system.tender.enums.TenderStatus;
import com.procurement.system.tender.enums.TenderType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tenders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tender {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @Column(nullable = false, length = 500)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TenderType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TenderMethod method;

    @Column(precision = 20, scale = 2)
    private BigDecimal estimatedValue;

    private LocalDateTime hsmtIssueDate;

    private LocalDateTime bidOpenDate;

    private LocalDateTime bidDeadline;

    private Integer contractDuration;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TenderStatus status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
