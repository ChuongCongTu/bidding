package com.procurement.system.user.dto;

import com.procurement.system.user.enums.UserRole;
import com.procurement.system.user.enums.UserStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class UserResponse {
    private UUID id;
    private String email;
    private UserRole role;
    private UserStatus status;
    private String fullName;
    private String taxCode;
    private String address;
    private String phone;
    private String organizationName;
    private String companyName;
    private String representativeName;
    private LocalDateTime createdAt;
}
