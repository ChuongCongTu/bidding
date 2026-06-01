package com.procurement.system.plan.dto;

import com.procurement.system.plan.enums.PlanStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeStatusRequest {
    @NotNull
    private PlanStatus status;
}
