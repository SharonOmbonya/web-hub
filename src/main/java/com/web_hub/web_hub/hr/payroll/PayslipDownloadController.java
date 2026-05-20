package com.web_hub.web_hub.hr.payroll;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payslips")
@RequiredArgsConstructor
public class PayslipDownloadController {

    private final PayrollRepository payrollRepository;
    private final PayslipPdfService pdfService;

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadPayslip(@PathVariable Long id) {

        Payroll payroll = payrollRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payslip not found"));

        byte[] file = pdfService.generatePayslip(payroll.getId());

        String filename = "payslip_" + payroll.getMonth() + "_" + payroll.getYear() + ".txt";

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                .header("Content-Type", "application/octet-stream")
                .header("Content-Length", String.valueOf(file.length))
                .body(file);
    }
    }
