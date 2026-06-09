package com.web_hub.web_hub.assetManagement.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "assets")
@Data
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String assetCode; // e.g., "AS001"

    @Column(nullable = false)
    private String name; // e.g., "MacBook Pro 14\" M3"

    @Column(nullable = false)
    private String category; // e.g., "Laptop"

    @Column(nullable = false, unique = true)
    private String serialNumber; // e.g., "MBP14-2024-001"

    // Storing assignment details. (If you have a separate Employee/User model later,
    // you can turn these into a @ManyToOne relationship).
    private String assignedToName;
    private String assignedToDepartment;

    private LocalDate assignmentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "asset_condition")
    private AssetCondition assetCondition;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssetStatus status;
}