package com.procurement.system.user;

import com.procurement.system.user.enums.UserRole;
import com.procurement.system.user.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    List<User> findAllByRoleAndStatus(UserRole role, UserStatus status);
    List<User> findAllByRole(UserRole role);
    List<User> findAllByStatus(UserStatus status);
    boolean existsByRole(UserRole role);
}
