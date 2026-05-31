package com.procurement.system.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {
    private String fullName;
    private String taxCode;
    private String address;
    private String phone;
    private String organizationName;
    private String companyName;
    private String representativeName;
}
