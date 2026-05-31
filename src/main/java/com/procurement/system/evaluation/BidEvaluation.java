package com.procurement.system.evaluation;

import com.procurement.system.bidsubmission.BidSubmission;
import com.procurement.system.evaluation.enums.EvaluationResult;
import com.procurement.system.tender.Tender;
import com.procurement.system.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bid_evaluations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BidEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tender_id", nullable = false)
    private Tender tender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bid_submission_id", nullable = false)
    private BidSubmission bidSubmission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluator_id", nullable = false)
    private User evaluator;

    @Column(precision = 5, scale = 2)
    private BigDecimal technicalScore;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private EvaluationResult result;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime evaluatedAt;
}
