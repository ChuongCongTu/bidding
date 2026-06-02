package com.procurement.system.tender.dto;

import com.procurement.system.tender.enums.TenderMethod;
import com.procurement.system.tender.enums.TenderType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CreateTenderRequest {

    @NotNull
    private UUID planId;

    @NotBlank
    private String name;

    @NotNull
    private TenderType type;

    @NotNull
    private TenderMethod method;

    private BigDecimal estimatedValue;

    @Future
    private LocalDateTime bidDeadline;

    private Integer contractDuration;
}
