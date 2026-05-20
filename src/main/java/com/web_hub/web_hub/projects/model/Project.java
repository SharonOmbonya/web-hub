package com.web_hub.web_hub.projects.model;

import com.web_hub.web_hub.hr.Employees.Employee;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String client;

    private Double budget;

    private LocalDate startDate;

    private LocalDate endDate;

    private String status; // ON_TRACK, AT_RISK, DELAYED

    @ManyToMany
    @JoinTable(
            name = "project_employees",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "employee_id")
    )
    private List<Employee> employees;
}
