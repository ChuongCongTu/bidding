package com.procurement.system.user.dto;

import com.procurement.system.user.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8)
    private String password;

    @NotNull
    private UserRole role;

    @NotBlank
    private String fullName;

    private String taxCode;
    private String address;
    private String phone;
    private String organizationName;
    private String companyName;
    private String representativeName;
}
