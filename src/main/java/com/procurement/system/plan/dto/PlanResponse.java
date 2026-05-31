package com.procurement.system.plan.dto;

import com.procurement.system.plan.enums.PlanStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class PlanResponse {
    private UUID id;
    private UUID investorId;
    private String investorName;
    private String name;
    private String code;
    private BigDecimal totalBudget;
    private Integer fiscalYear;
    private String description;
    private PlanStatus status;
    private LocalDateTime createdAt;
}
