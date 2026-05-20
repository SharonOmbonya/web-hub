package com.web_hub.web_hub.projects.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "project_assignments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long projectId;

    private Long employeeId;
}
