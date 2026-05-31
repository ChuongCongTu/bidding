package com.procurement.system.plan;

import com.procurement.system.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlanRepository extends JpaRepository<Plan, UUID> {
    List<Plan> findAllByInvestor(User investor);
    Optional<Plan> findByIdAndInvestor(UUID id, User investor);
    long countByFiscalYear(Integer fiscalYear);
}
