package com.web_hub.web_hub.admin.assetmanagement;

import com.web_hub.web_hub.hr.Employees.Employee;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;        // e.g. Laptop
    private String type;        // e.g. Electronics
    private String serialNumber;

    private String status;      // AVAILABLE, ASSIGNED

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    private LocalDateTime assignedAt;
}
