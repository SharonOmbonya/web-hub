package com.web_hub.web_hub.hr.issuewarnings;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "employee_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long employeeId;

    @Enumerated(EnumType.STRING)
    private RecordType type;

    private String title;

    private String description;

    private String severity; // optional

    private Long issuedBy;

    private LocalDate dateIssued;
}
