package com.procurement.system.tender;

import com.procurement.system.common.ApiResponse;
import com.procurement.system.tender.dto.ChangeTenderStatusRequest;
import com.procurement.system.tender.dto.CreateTenderRequest;
import com.procurement.system.tender.dto.TenderResponse;
import com.procurement.system.tender.dto.UpdateTenderRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/investor/tenders")
public class TenderController {

    private final TenderService tenderService;

    @PostMapping
    public ResponseEntity<ApiResponse<TenderResponse>> createTender(@RequestBody @Valid CreateTenderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(tenderService.createTender(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TenderResponse>> updateTender(@PathVariable UUID id,@RequestBody @Valid UpdateTenderRequest request) {
        return ResponseEntity.ok(ApiResponse.success(tenderService.updateTender(id, request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TenderResponse>>> getMyTenders(@RequestParam UUID planId) {
        return ResponseEntity.ok(ApiResponse.success(tenderService.getMyTenders(planId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TenderResponse>> getMyTenderById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(tenderService.getMyTenderById(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTender(@PathVariable UUID id) {
        tenderService.deleteTender(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<TenderResponse>> changeStatus(@PathVariable UUID id, @RequestBody @Valid ChangeTenderStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.success(tenderService.changeStatus(id, request.getStatus())));
    }
}
