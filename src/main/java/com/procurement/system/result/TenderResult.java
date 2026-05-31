package com.procurement.system.result;

import com.procurement.system.bidsubmission.BidSubmission;
import com.procurement.system.tender.Tender;
import com.procurement.system.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tender_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenderResult {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tender_id", nullable = false, unique = true)
    private Tender tender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winning_contractor_id")
    private User winningContractor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winning_submission_id")
    private BidSubmission winningSubmission;

    @Column(precision = 20, scale = 2)
    private BigDecimal contractPrice;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime announcedAt;
}
