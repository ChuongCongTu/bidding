package com.procurement.system.tender;

import com.procurement.system.common.ApiResponse;
import com.procurement.system.tender.dto.TenderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tenders/public")
public class TenderPublicController {

    private final TenderService tenderService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TenderResponse>>> getPublicTenders() {
        return ResponseEntity.ok(ApiResponse.success(tenderService.getPublicTenders()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TenderResponse>> getPublicTenderById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(tenderService.getPublicTenderById(id)));
    }
}
