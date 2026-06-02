package com.procurement.system.tender.dto;

import com.procurement.system.tender.enums.TenderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeTenderStatusRequest {

    @NotNull
    private TenderStatus status;
}
