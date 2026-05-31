package com.procurement.system.plan.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UpdatePlanRequest {

    private String name;

    private BigDecimal totalBudget;

    private Integer fiscalYear;

    private String description;
}
