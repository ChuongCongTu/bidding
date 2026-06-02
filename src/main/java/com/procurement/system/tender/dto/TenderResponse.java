package com.procurement.system.tender.dto;

import com.procurement.system.tender.enums.TenderMethod;
import com.procurement.system.tender.enums.TenderStatus;
import com.procurement.system.tender.enums.TenderType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class TenderResponse {

    private UUID id;
    private UUID planId;
    private String planCode;
    private String name;
    private String code;
    private TenderType type;
    private TenderMethod method;
    private BigDecimal estimatedValue;
    private LocalDateTime hsmtIssueDate;
    private LocalDateTime bidOpenDate;
    private LocalDateTime bidDeadline;
    private Integer contractDuration;
    private TenderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
