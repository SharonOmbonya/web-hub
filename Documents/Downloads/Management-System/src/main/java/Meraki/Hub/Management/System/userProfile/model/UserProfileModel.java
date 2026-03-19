package Meraki.Hub.Management.System.userProfile.model;

import Meraki.Hub.Management.System.user.model.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String role;
    private String adminSection;
    private String additionalAdminDetails;
    private String departmentAssigned;
    private String teamMembers;
    private String hrManagerNotes;
    private String departmentName;
    private String reportingManager;
    private String departmentGoals;
    private String jobTitle;
    private String startDate;
    private String salary;
    private String employeeBenefits;

}
