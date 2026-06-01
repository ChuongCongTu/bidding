package com.procurement.system.plan;

import com.procurement.system.common.ApiResponse;
import com.procurement.system.plan.dto.ChangeStatusRequest;
import com.procurement.system.plan.dto.CreatePlanRequest;
import com.procurement.system.plan.dto.PlanResponse;
import com.procurement.system.plan.dto.UpdatePlanRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/investor/plans")
public class PlanController {

    private final PlanService planService;

    @PostMapping
    public ResponseEntity<ApiResponse<PlanResponse>> createPlan(@RequestBody @Valid CreatePlanRequest createPlanRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(planService.createPlan(createPlanRequest)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PlanResponse>>> getMyPlans() {
        return ResponseEntity.ok(ApiResponse.success(planService.getMyPlans()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PlanResponse>> getMyPlanById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(planService.getMyPlanById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PlanResponse>> updatePlan(@PathVariable UUID id, @RequestBody @Valid UpdatePlanRequest updatePlanRequest) {
        return ResponseEntity.ok(ApiResponse.success(planService.updatePlan(id, updatePlanRequest)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<PlanResponse>> changeStatus(@PathVariable UUID id, @RequestBody @Valid ChangeStatusRequest changeStatusRequest) {
        return ResponseEntity.ok(ApiResponse.success(planService.changeStatus(id, changeStatusRequest.getStatus())));
    }
}
