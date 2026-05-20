package com.web_hub.web_hub.employeemodule.trainings;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/certifications")
@RequiredArgsConstructor
public class CertificationController {

    private final CertificationService certificationService;

    @PostMapping("/issue")
    public Certification issueCertificate(
            @RequestParam Long employeeId,
            @RequestParam Long trainingId,
            @RequestParam String name
    ) {
        return certificationService.issueCertificate(employeeId, trainingId, name);
    }

    @GetMapping("/employee/{employeeId}")
    public List<Certification> getEmployeeCertificates(@PathVariable Long employeeId) {
        return certificationService.getEmployeeCertificates(employeeId);
    }
}
