package com.web_hub.web_hub.admin.auditlog;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String action;
    private String entity;
    private Long entityId;

    private String performedBy;
    private String role;

    private String details;

    private LocalDateTime timestamp;
}
