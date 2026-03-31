package com.web_hub.web_hub.hr.leave;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
@Data
@Entity
@Table(name = "leaves")
public class Leave {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long employeeId;
    private Long userId; // 👈 link to existing user

    private String type;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private String reason;
}
