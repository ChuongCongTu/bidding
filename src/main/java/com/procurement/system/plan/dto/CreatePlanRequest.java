package com.procurement.system.plan.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreatePlanRequest {

    @NotBlank
    private String name;

    private BigDecimal totalBudget;

    private Integer fiscalYear;

    private String description;
}