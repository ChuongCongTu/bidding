package com.procurement.system.tender.dto;

import com.procurement.system.tender.enums.TenderMethod;
import com.procurement.system.tender.enums.TenderType;
import jakarta.validation.constraints.Future;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class UpdateTenderRequest {

    private String name;

    private TenderType type;

    private TenderMethod method;

    private BigDecimal estimatedValue;

    @Future
    private LocalDateTime bidDeadline;

    private Integer contractDuration;
}
