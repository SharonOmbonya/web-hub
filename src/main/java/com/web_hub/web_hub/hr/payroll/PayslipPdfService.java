package com.web_hub.web_hub.hr.payroll;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.web_hub.web_hub.hr.Employees.Employee;
import com.web_hub.web_hub.hr.Employees.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class PayslipPdfService {

    private final EmployeeRepository employeeRepository;

    public byte[] generatePayslip(Long id) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            // ===== Title =====
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("PAYSLIP", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph(" ")); // space

            // ===== Employee Details =====
            PdfPTable detailsTable = new PdfPTable(2);
            detailsTable.setWidthPercentage(100);

            detailsTable.addCell("Employee ID:");
            detailsTable.addCell(String.valueOf(id));

            detailsTable.addCell("Employee Name:");
            detailsTable.addCell("John Doe"); // replace with real data

            detailsTable.addCell("Position:");
            detailsTable.addCell("Backend Developer");

            detailsTable.addCell("Month:");
            detailsTable.addCell("April 2026");

            document.add(detailsTable);

            document.add(new Paragraph(" ")); // space

            // ===== Salary Breakdown =====
            PdfPTable salaryTable = new PdfPTable(2);
            salaryTable.setWidthPercentage(100);

            salaryTable.addCell("Basic Salary");
            salaryTable.addCell("50,000");

            salaryTable.addCell("Allowances");
            salaryTable.addCell("5,000");

            salaryTable.addCell("Deductions");
            salaryTable.addCell("2,000");

            salaryTable.addCell("Net Salary");
            salaryTable.addCell("53,000");

            document.add(salaryTable);

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Thank you for your service."));

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }

        return out.toByteArray();
    }
}
