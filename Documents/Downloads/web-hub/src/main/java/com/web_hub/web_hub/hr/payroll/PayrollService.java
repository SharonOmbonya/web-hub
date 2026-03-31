package com.web_hub.web_hub.hr.payroll;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PayrollService {

    private final PayrollRepository payrollRepository;

    public PayrollResponse runPayroll(CreatePayrollRequest request) {

        double grossPay = request.getBasicSalary() + request.getHouseAllowance();

        double taxablePay = grossPay;

        double payAfterTax = taxablePay - request.getIncomeTax() - request.getPaye();

        double netPay = payAfterTax - request.getShif() - request.getHousingLevy()
                + request.getPersonalRelief();

        Payroll payroll = Payroll.builder()
                .employeeId(request.getEmployeeId())
                .month(request.getMonth())
                .year(request.getYear())
                .basicSalary(request.getBasicSalary())
                .houseAllowance(request.getHouseAllowance())
                .shif(request.getShif())
                .housingLevy(request.getHousingLevy())
                .incomeTax(request.getIncomeTax())
                .personalRelief(request.getPersonalRelief())
                .paye(request.getPaye())
                .grossPay(grossPay)
                .taxablePay(taxablePay)
                .payAfterTax(payAfterTax)
                .netPay(netPay)
                .status("PENDING")
                .build();

        Payroll saved = payrollRepository.save(payroll);

        return mapToResponse(saved);
    }

    public List<PayrollResponse> getAllPayroll() {
        return payrollRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<PayrollResponse> getByEmployee(Long employeeId) {
        return payrollRepository.findByEmployeeId(employeeId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private PayrollResponse mapToResponse(Payroll payroll) {
        return PayrollResponse.builder()
                .id(payroll.getId())
                .employeeId(payroll.getEmployeeId())
                .month(payroll.getMonth())
                .year(payroll.getYear())
                .grossPay(payroll.getGrossPay())
                .netPay(payroll.getNetPay())
                .status(payroll.getStatus())
                .build();
    }
}
