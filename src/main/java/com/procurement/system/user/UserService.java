package com.procurement.system.user;

import com.procurement.system.common.exception.BusinessException;
import com.procurement.system.common.exception.ResourceNotFoundException;
import com.procurement.system.user.dto.CreateUserRequest;
import com.procurement.system.user.dto.UpdateUserRequest;
import com.procurement.system.user.dto.UserResponse;
import com.procurement.system.user.enums.UserRole;
import com.procurement.system.user.enums.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already exists");
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setStatus(UserStatus.ACTIVE);
        user.setFullName(request.getFullName());
        user.setTaxCode(request.getTaxCode());
        user.setAddress(request.getAddress());
        user.setPhone(request.getPhone());
        user.setOrganizationName(request.getOrganizationName());
        user.setCompanyName(request.getCompanyName());
        user.setRepresentativeName(request.getRepresentativeName());

        return toResponse(userRepository.save(user));
    }

    public List<UserResponse> getUsers(UserRole role, UserStatus status) {
        List<User> users;
        if (role == null && status == null) {
            users = userRepository.findAll();
        } else if (role != null && status != null) {
            users = userRepository.findAllByRoleAndStatus(role, status);
        } else if (role != null) {
            users = userRepository.findAllByRole(role);
        } else {
            users = userRepository.findAllByStatus(status);
        }
        return users.stream().map(this::toResponse).toList();
    }

    public UserResponse getUserById(UUID id) {
        return userRepository.findById(id).map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public UserResponse updateUser(UUID id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setFullName(request.getFullName());
        user.setTaxCode(request.getTaxCode());
        user.setAddress(request.getAddress());
        user.setPhone(request.getPhone());
        user.setOrganizationName(request.getOrganizationName());
        user.setCompanyName(request.getCompanyName());
        user.setRepresentativeName(request.getRepresentativeName());

        return toResponse(userRepository.save(user));
    }

    public void changeStatus(UUID id, UserStatus status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setStatus(status);
        userRepository.save(user);
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
                .fullName(user.getFullName())
                .taxCode(user.getTaxCode())
                .address(user.getAddress())
                .phone(user.getPhone())
                .organizationName(user.getOrganizationName())
                .companyName(user.getCompanyName())
                .representativeName(user.getRepresentativeName())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
