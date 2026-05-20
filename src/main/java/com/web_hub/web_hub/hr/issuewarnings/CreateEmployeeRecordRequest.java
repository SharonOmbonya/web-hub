package com.web_hub.web_hub.hr.issuewarnings;

import lombok.Data;

@Data
public class CreateEmployeeRecordRequest {

    private Long employeeId;
    private RecordType type;
    private String title;
    private String description;
    private String severity;
}
