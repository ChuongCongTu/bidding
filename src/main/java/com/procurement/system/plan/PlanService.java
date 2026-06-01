package com.procurement.system.plan;

import com.procurement.system.common.exception.BusinessException;
import com.procurement.system.common.exception.ResourceNotFoundException;
import com.procurement.system.plan.dto.ChangeStatusRequest;
import com.procurement.system.plan.dto.CreatePlanRequest;
import com.procurement.system.plan.dto.PlanResponse;
import com.procurement.system.plan.dto.UpdatePlanRequest;
import com.procurement.system.plan.enums.PlanStatus;
import com.procurement.system.user.User;
import com.procurement.system.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlanService {
    private final PlanRepository planRepository;
    private final UserRepository userRepository;

    private UUID getCurrentInvestorId() {
        String userId = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return UUID.fromString(userId);
    }

    private void validateTransition(PlanStatus current, PlanStatus target) {
        boolean valid = switch (current) {
            case DRAFT -> target == PlanStatus.IN_PROGRESS || target == PlanStatus.CANCELLED;
            case IN_PROGRESS -> target == PlanStatus.COMPLETED || target == PlanStatus.CANCELLED;
            default -> false;
        };
        if (!valid) throw new BusinessException("Cannot transition from " + current + " to " + target);
    }

    public PlanResponse createPlan(CreatePlanRequest request) {
        User investor = userRepository.findById(getCurrentInvestorId())
                .orElseThrow(() -> new ResourceNotFoundException("Investor not found"));

        Integer fiscalYear = request.getFiscalYear() != null ? request.getFiscalYear() : LocalDate.now().getYear();

        for (int attempt = 0; attempt < 5; attempt++) {
            try {
                long count = planRepository.countByFiscalYear(fiscalYear);
                String code = "KH-%d-%04d".formatted(fiscalYear, count + 1);

                Plan plan = new Plan();
                plan.setCode(code);
                plan.setName(request.getName());
                plan.setFiscalYear(fiscalYear);
                plan.setTotalBudget(request.getTotalBudget());
                plan.setStatus(PlanStatus.DRAFT);
                plan.setInvestor(investor);
                plan.setDescription(request.getDescription());
                planRepository.save(plan);
                return toResponse(plan);
            } catch (DataIntegrityViolationException e) {
                if (attempt == 4) throw new BusinessException("Cannot create plan, please retry");
            }
        }
        throw new BusinessException("Cannot create plan");
    }

    @Transactional(readOnly = true)
    public List<PlanResponse> getMyPlans() {
        User investor = userRepository.findById(getCurrentInvestorId())
                .orElseThrow(() -> new ResourceNotFoundException("Investor not found"));

        List<Plan> plans = planRepository.findAllByInvestor(investor);

        return plans.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PlanResponse getMyPlanById (UUID id) {
        User investor = userRepository.findById(getCurrentInvestorId())
                .orElseThrow(() -> new ResourceNotFoundException("Investor not found"));

        Plan plan = planRepository.findByIdAndInvestor(id, investor)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));

        return toResponse(plan);
    }

    @Transactional
    public PlanResponse updatePlan(UUID id, UpdatePlanRequest request) {
        User investor = userRepository.findById(getCurrentInvestorId())
                .orElseThrow(() -> new ResourceNotFoundException("Investor not found"));

        Plan plan = planRepository.findByIdAndInvestor(id, investor)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));

        if (plan.getStatus() != PlanStatus.DRAFT) {
            throw  new BusinessException("Only DRAFT plans can be updated");
        }

        if (request.getName() != null) {
            plan.setName(request.getName());
        }

        if (request.getTotalBudget() != null) {
            plan.setTotalBudget(request.getTotalBudget());
        }

        if (request.getFiscalYear() != null) {
            plan.setFiscalYear(request.getFiscalYear());
        }

        if (request.getDescription() != null) {
            plan.setDescription(request.getDescription());
        }

        return toResponse(plan);
    }

    @Transactional
    public PlanResponse changeStatus(UUID id, PlanStatus targetStatus) {
        User investor = userRepository.findById(getCurrentInvestorId())
                .orElseThrow(() -> new ResourceNotFoundException("Investor not found"));

        Plan plan = planRepository.findByIdAndInvestor(id, investor)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));

        validateTransition(plan.getStatus(), targetStatus);

        plan.setStatus(targetStatus);

        return toResponse(plan);
    }

    private PlanResponse toResponse(Plan plan) {
        return PlanResponse.builder()
                .id(plan.getId())
                .investorId(plan.getInvestor().getId())
                .investorName(plan.getInvestor().getFullName())
                .name(plan.getName())
                .description(plan.getDescription())
                .code(plan.getCode())
                .totalBudget(plan.getTotalBudget())
                .fiscalYear(plan.getFiscalYear())
                .status(plan.getStatus())
                .createdAt(plan.getCreatedAt())
                .build();
    }
}
