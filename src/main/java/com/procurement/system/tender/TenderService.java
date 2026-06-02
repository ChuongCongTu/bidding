package com.procurement.system.tender;

import com.procurement.system.common.exception.BusinessException;
import com.procurement.system.common.exception.ResourceNotFoundException;
import com.procurement.system.plan.Plan;
import com.procurement.system.plan.PlanRepository;
import com.procurement.system.tender.dto.CreateTenderRequest;
import com.procurement.system.tender.dto.TenderResponse;
import com.procurement.system.tender.dto.UpdateTenderRequest;
import com.procurement.system.tender.enums.TenderStatus;
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
public class TenderService {
    private final TenderRepository tenderRepository;
    private final UserRepository userRepository;
    private final PlanRepository planRepository;

    public TenderResponse createTender(CreateTenderRequest  request) {
        User investor = userRepository.findById(getCurrentInvestorId())
                .orElseThrow(() -> new ResourceNotFoundException("Investor not found"));

        Plan plan = planRepository.findByIdAndInvestor(request.getPlanId(), investor)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));

        Integer fiscalYear = plan.getFiscalYear() != null ? plan.getFiscalYear() : LocalDate.now().getYear();

        for (int attempt = 0; attempt < 5; attempt++) {
            try {
                long count = tenderRepository.countByFiscalYear(fiscalYear);
                String code = "GT-%d-%04d".formatted(fiscalYear, count + 1);

                Tender tender = new Tender();
                tender.setCode(code);
                tender.setPlan(plan);
                tender.setType(request.getType());
                tender.setMethod(request.getMethod());
                tender.setEstimatedValue(request.getEstimatedValue());
                tender.setBidDeadline(request.getBidDeadline());
                tender.setContractDuration(request.getContractDuration());
                tender.setName(request.getName());
                tender.setStatus(TenderStatus.NEW);
                tenderRepository.save(tender);
                return toResponse(tender);
            } catch (DataIntegrityViolationException e) {
                if (attempt == 4) throw new BusinessException("Cannot create tender, please retry");
            }
        }
        throw new BusinessException("Cannot create tender");
    }

    @Transactional
    public TenderResponse updateTender(UUID id, UpdateTenderRequest request) {
        User investor = userRepository.findById(getCurrentInvestorId())
                .orElseThrow(() -> new ResourceNotFoundException("Investor not found"));

        Tender tender = tenderRepository.findByIdAndInvestorId(id, investor.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Tender not found"));

        if (!tender.getStatus().equals(TenderStatus.NEW)) {
            throw new BusinessException("Cannot update tender");
        }

        if (request.getName() != null) {
            tender.setName(request.getName());
        }

        if (request.getType() != null) {
            tender.setType(request.getType());
        }

        if (request.getMethod() != null) {
            tender.setMethod(request.getMethod());
        }

        if (request.getEstimatedValue() != null) {
            tender.setEstimatedValue(request.getEstimatedValue());
        }

        if (request.getBidDeadline() != null) {
            tender.setBidDeadline(request.getBidDeadline());
        }

        if (request.getContractDuration() != null) {
            tender.setContractDuration(request.getContractDuration());
        }

        return toResponse(tender);
    }

    @Transactional(readOnly = true)
    public List<TenderResponse> getMyTenders(UUID planId) {
        User investor = userRepository.findById(getCurrentInvestorId())
                .orElseThrow(() -> new ResourceNotFoundException("Investor not found"));

        Plan plan = planRepository.findByIdAndInvestor(planId, investor)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));

        List<Tender> tenders = tenderRepository.findAllByPlan(plan);
        return tenders.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public TenderResponse getMyTenderById(UUID id) {
        User investor = userRepository.findById(getCurrentInvestorId())
                .orElseThrow(() -> new ResourceNotFoundException("Investor not found"));

        Tender tender = tenderRepository.findByIdAndInvestorId(id, investor.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Tender not found"));

        return toResponse(tender);
    }

    @Transactional
    public void deleteTender(UUID id) {
        User investor = userRepository.findById(getCurrentInvestorId())
                .orElseThrow(() -> new ResourceNotFoundException("Investor not found"));

        Tender tender = tenderRepository.findByIdAndInvestorId(id, investor.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Tender not found"));

        if (tender.getStatus() != TenderStatus.NEW) {
            throw new BusinessException("Only NEW tenders can be deleted");
        }
        tenderRepository.delete(tender);
    }

    @Transactional
    public TenderResponse changeStatus(UUID id, TenderStatus status) {
        User investor = userRepository.findById(getCurrentInvestorId())
                .orElseThrow(() -> new ResourceNotFoundException("Investor not found"));

        Tender tender = tenderRepository.findByIdAndInvestorId(id, investor.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Tender not found"));

        validateTransition(tender.getStatus(), status);

        tender.setStatus(status);

        return toResponse(tender);
    }

    @Transactional(readOnly = true)
    public List<TenderResponse> getPublicTenders() {
        List<TenderStatus> tenderStatuses = List.of(TenderStatus.PUB_MT, TenderStatus.OPEN_BID, TenderStatus.PUB_KQLCNT);
        List<Tender> tenders = tenderRepository.findAllByStatusIn(tenderStatuses);

        return tenders.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public TenderResponse getPublicTenderById(UUID id) {
        Tender tender = tenderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tender not found"));
        List<TenderStatus> tenderStatuses = List.of(TenderStatus.PUB_MT, TenderStatus.OPEN_BID, TenderStatus.PUB_KQLCNT);
        if (!tenderStatuses.contains(tender.getStatus())) {
            throw new ResourceNotFoundException("Tender not found");
        }

        return toResponse(tender);
    }

    private UUID getCurrentInvestorId() {
        String userId = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return UUID.fromString(userId);
    }

    private void validateTransition(TenderStatus current, TenderStatus target) {
        boolean valid = switch (current) {
            case NEW -> target == TenderStatus.INIT_MT;
            case INIT_MT -> target == TenderStatus.PUB_MT || target == TenderStatus.CANCEL_BID;
            case PUB_MT -> target == TenderStatus.OPEN_BID || target == TenderStatus.CANCEL_BID;
            case OPEN_BID -> target == TenderStatus.PUB_KQLCNT || target == TenderStatus.CANCEL_BID;
            default -> false;
        };
        if (!valid) throw new BusinessException("Cannot transition from " + current + " to " + target);
    }

    private TenderResponse toResponse(Tender tender) {
        return TenderResponse.builder()
                .id(tender.getId())
                .planId(tender.getPlan().getId())
                .planCode(tender.getPlan().getCode())
                .name(tender.getName())
                .code(tender.getCode())
                .type(tender.getType())
                .method(tender.getMethod())
                .estimatedValue(tender.getEstimatedValue())
                .hsmtIssueDate(tender.getHsmtIssueDate())
                .bidOpenDate(tender.getBidOpenDate())
                .bidDeadline(tender.getBidDeadline())
                .contractDuration(tender.getContractDuration())
                .status(tender.getStatus())
                .createdAt(tender.getCreatedAt())
                .updatedAt(tender.getUpdatedAt())
                .build();
    }

}
