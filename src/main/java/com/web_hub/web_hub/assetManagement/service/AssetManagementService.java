package com.web_hub.web_hub.assetManagement.service;


import com.web_hub.web_hub.assetManagement.api.dto.AssetAssignmentRequestDto;
import com.web_hub.web_hub.assetManagement.api.dto.AssetCreateRequestDto;
import com.web_hub.web_hub.assetManagement.api.dto.AssetDashboardSummaryDto;
import com.web_hub.web_hub.assetManagement.api.dto.AssetResponseDto;
import com.web_hub.web_hub.assetManagement.model.Asset;
import com.web_hub.web_hub.assetManagement.model.AssetStatus;
import com.web_hub.web_hub.assetManagement.repository.AssetManagementRepository;
import com.web_hub.web_hub.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AssetManagementService {

    private final AssetManagementRepository assetRepository;

    // 1. GET DASHBOARD METRICS
    @Transactional(readOnly = true)
    public AssetDashboardSummaryDto getDashboardSummary() {
        long total = assetRepository.count();
        long assigned = assetRepository.countByStatus(AssetStatus.ASSIGNED);
        long available = assetRepository.countByStatus(AssetStatus.AVAILABLE);
        long inRepair = assetRepository.countByStatus(AssetStatus.IN_REPAIR);

        return new AssetDashboardSummaryDto(total, assigned, available, inRepair);
    }

    // 2. GET ALL / FILTERED ASSETS (Routes explicitly based on presence of params)
    @Transactional(readOnly = true)
    public List<AssetResponseDto> getAllAssets(String search, AssetStatus status) {
        String cleanSearch = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        boolean hasSearch = cleanSearch != null;

        List<Asset> assets;

        if (status != null && hasSearch) {
            assets = assetRepository.findByStatusAndSearch(status, cleanSearch);
        } else if (status != null) {
            assets = assetRepository.findByStatus(status);
        } else if (hasSearch) {
            assets = assetRepository.findBySearch(cleanSearch);
        } else {
            assets = assetRepository.findAll();
        }

        return assets.stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    // 3. CREATE NEW ASSET
    @Transactional
    public AssetResponseDto createAsset(AssetCreateRequestDto dto) {
        if (assetRepository.existsBySerialNumber(dto.getSerialNumber())) {
            throw new DataIntegrityViolationException("Asset with serial number '" + dto.getSerialNumber() + "' already exists");
        }

        Asset asset = new Asset();
        asset.setName(dto.getName());
        asset.setCategory(dto.getCategory());
        asset.setSerialNumber(dto.getSerialNumber());
        asset.setAssetCondition(dto.getAssetCondition());
        asset.setStatus(AssetStatus.AVAILABLE); // New assets default to available

        // Dynamic code generation: e.g., AS001, AS002
        asset.setAssetCode(generateNextAssetCode());

        return mapToResponseDto(assetRepository.save(asset));
    }

    // 4. ASSIGN ASSET TO EMPLOYEE
    @Transactional
    public AssetResponseDto assignAsset(Long id, AssetAssignmentRequestDto dto) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with id: " + id));

        asset.setAssignedToName(dto.getAssignedToName());
        asset.setAssignedToDepartment(dto.getAssignedToDepartment());
        asset.setAssignmentDate(LocalDate.now());
        asset.setStatus(AssetStatus.ASSIGNED);

        return mapToResponseDto(assetRepository.save(asset));
    }

    // 5. UNASSIGN ASSET (Return to Available Pool)
    @Transactional
    public AssetResponseDto unassignAsset(Long id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with id: " + id));

        asset.setAssignedToName(null);
        asset.setAssignedToDepartment(null);
        asset.setAssignmentDate(null);
        asset.setStatus(AssetStatus.AVAILABLE);

        return mapToResponseDto(assetRepository.save(asset));
    }

    // --- Private Helper Methods ---

    private String generateNextAssetCode() {
        return assetRepository.findFirstByOrderByIdDesc()
                .map(lastAsset -> {
                    long nextNumber = lastAsset.getId() + 1;
                    return String.format("AS%03d", nextNumber);
                })
                .orElse("AS001");
    }

    private AssetResponseDto mapToResponseDto(Asset asset) {
        return new AssetResponseDto(
                asset.getId(),
                asset.getAssetCode(),
                asset.getName(),
                asset.getCategory(),
                asset.getSerialNumber(),
                asset.getAssignedToName(),
                asset.getAssignedToDepartment(),
                asset.getAssignmentDate(),
                asset.getAssetCondition(),
                asset.getStatus()
        );
    }
}