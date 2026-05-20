package com.web_hub.web_hub.admin.assetmanagement;

import com.web_hub.web_hub.admin.AssetRequest;
import com.web_hub.web_hub.admin.AssetResponse;
import com.web_hub.web_hub.admin.auditlog.AuditLogService;
import com.web_hub.web_hub.hr.Employees.Employee;
import com.web_hub.web_hub.hr.Employees.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AssetService {

    private final AssetRepository assetRepository;
    private final EmployeeRepository employeeRepository;
    private final AuditLogService auditLogService;

    /* ================= CREATE ================= */
    public AssetResponse createAsset(AssetRequest request) {

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Asset asset = Asset.builder()
                .name(request.getName())
                .type(request.getType())
                .serialNumber(request.getSerialNumber())
                .status("ASSIGNED")
                .employee(employee)
                .assignedAt(LocalDateTime.now())
                .build();

        Asset saved = assetRepository.save(asset);


        auditLogService.logAction(
                "ASSIGN",
                "Asset",
                saved.getId(),
                employee.getEmail(),
                "HR",
                "Assigned " + saved.getName() + " to " + employee.getFirstName() + " " + employee.getLastName()        );

        return mapToResponse(saved);
    }

    /* ================= GET ALL ================= */
    public List<AssetResponse> getAllAssets() {
        return assetRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /* ================= RETURN ASSET ================= */
    public void returnAsset(Long id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found"));

        asset.setStatus("AVAILABLE");
        asset.setEmployee(null);

        assetRepository.save(asset);

        // 🔥 AUDIT LOG
        auditLogService.logAction(
                "RETURN",
                "Asset",
                asset.getId(),
                "HR",
                "HR",
                "Returned asset"
        );
    }

    /* ================= MAPPER ================= */
    private AssetResponse mapToResponse(Asset asset) {
        return new AssetResponse(
                asset.getId(),
                asset.getName(),
                asset.getType(),
                asset.getSerialNumber(),
                asset.getStatus(),
                asset.getEmployee() != null
                        ? asset.getEmployee().getFirstName() + " " + asset.getEmployee().getLastName()
                        : null,
                asset.getEmployee() != null
                        ? asset.getEmployee().getEmail()
                        : null
        );
    }
}
