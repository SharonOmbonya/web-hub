package com.web_hub.web_hub.assetManagement.api.controller;

import com.web_hub.web_hub.assetManagement.api.dto.AssetAssignmentRequestDto;
import com.web_hub.web_hub.assetManagement.api.dto.AssetCreateRequestDto;
import com.web_hub.web_hub.assetManagement.api.dto.AssetDashboardSummaryDto;
import com.web_hub.web_hub.assetManagement.api.dto.AssetResponseDto;
import com.web_hub.web_hub.assetManagement.model.AssetStatus;
import com.web_hub.web_hub.assetManagement.service.AssetManagementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assets/management")
public class ManagementAssetController {

    private final AssetManagementService assetManagementService;

    // Constructor injection for dependency management
    public ManagementAssetController(AssetManagementService assetManagementService) {
        this.assetManagementService = assetManagementService;
    }

    // 1. Get the 4 card counters for the top dashboard row
    @GetMapping("/summary")
    public ResponseEntity<AssetDashboardSummaryDto> getDashboardSummary() {
        AssetDashboardSummaryDto summary = assetManagementService.getDashboardSummary();
        return ResponseEntity.ok(summary);
    }

    // 2. Fetch/Filter table items (Supports search bar and status filter tabs)
    @GetMapping
    public ResponseEntity<List<AssetResponseDto>> getAllAssets(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) AssetStatus status) {
        List<AssetResponseDto> assets = assetManagementService.getAllAssets(search, status);
        return ResponseEntity.ok(assets);
    }

    // 3. Click "+ New Asset"
    @PostMapping
    public ResponseEntity<AssetResponseDto> createAsset(@RequestBody AssetCreateRequestDto dto) {
        AssetResponseDto createdAsset = assetManagementService.createAsset(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAsset);
    }

    // 4. Click "Reassign" action
    @PutMapping("/{id}/assign")
    public ResponseEntity<AssetResponseDto> assignAsset(
            @PathVariable Long id,
            @RequestBody AssetAssignmentRequestDto dto) {
        AssetResponseDto updatedAsset = assetManagementService.assignAsset(id, dto);
        return ResponseEntity.ok(updatedAsset);
    }

    // 5. Click "Unassign" action
    @PutMapping("/{id}/unassign")
    public ResponseEntity<AssetResponseDto> unassignAsset(@PathVariable Long id) {
        AssetResponseDto updatedAsset = assetManagementService.unassignAsset(id);
        return ResponseEntity.ok(updatedAsset);
    }
}