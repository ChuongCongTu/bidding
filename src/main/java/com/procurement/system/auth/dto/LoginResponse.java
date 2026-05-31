package com.procurement.system.auth.dto;

import com.procurement.system.user.enums.UserRole;
import lombok.*;

import java.util.UUID;

@Getter
@Builder
public class LoginResponse {
    private String token;
    private UUID userId;
    private String email;
    private UserRole role;
}

