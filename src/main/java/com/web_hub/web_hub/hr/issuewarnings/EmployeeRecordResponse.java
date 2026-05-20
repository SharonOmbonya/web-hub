package com.web_hub.web_hub.hr.issuewarnings;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class EmployeeRecordResponse {

    private Long id;
    private Long employeeId;
    private RecordType type;
    private String title;
    private String description;
    private String severity;
    private Long issuedBy;
    private LocalDate dateIssued;
}
