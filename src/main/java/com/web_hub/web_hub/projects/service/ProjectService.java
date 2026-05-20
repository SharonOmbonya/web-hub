package com.web_hub.web_hub.projects.service;

import com.web_hub.web_hub.projects.model.Project;
import com.web_hub.web_hub.projects.model.ProjectAssignment;
import com.web_hub.web_hub.projects.repository.ProjectAssignmentRepository;
import com.web_hub.web_hub.projects.repository.ProjectRepository;
import com.web_hub.web_hub.projects.api.dto.CreateProjectRequest;
import com.web_hub.web_hub.projects.api.dto.EmployeeProjectResponse;
import com.web_hub.web_hub.projects.api.dto.ProjectPLResponse;
import com.web_hub.web_hub.projects.api.dto.ProjectResponse;
import com.web_hub.web_hub.projects.milestones.Milestone;
import com.web_hub.web_hub.projects.milestones.MilestoneRepository;
import com.web_hub.web_hub.projects.api.dto.ProjectStatusReportResponse;
import com.web_hub.web_hub.projects.resourceutilization.ResourceUtilizationResponse;
import com.web_hub.web_hub.timesheets.Timesheet;
import com.web_hub.web_hub.timesheets.TimesheetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectAssignmentRepository assignmentRepository;
    private final TimesheetRepository timesheetRepository;
    private final MilestoneRepository milestoneRepository;
    /* =========================================================
       CREATE PROJECT
       ========================================================= */
    public ProjectResponse create(CreateProjectRequest request) {

        Project project = Project.builder()
                .name(request.getName())
                .client(request.getClient())
                .budget(request.getBudget())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(request.getStatus())
                .build();

        projectRepository.save(project);

        return mapToResponse(project);
    }

    /* =========================================================
       GET ALL PROJECTS
       ========================================================= */
    public List<ProjectResponse> getAll() {
        return projectRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /* =========================================================
       ASSIGN EMPLOYEE TO PROJECT
       ========================================================= */
    public void assignEmployee(Long projectId, Long employeeId) {

        projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        ProjectAssignment assignment = ProjectAssignment.builder()
                .projectId(projectId)
                .employeeId(employeeId)
                .build();

        assignmentRepository.save(assignment);
    }

    /* =========================================================
       GET PROJECT MEMBERS
       ========================================================= */
    public List<Long> getProjectMembers(Long projectId) {

        projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        return assignmentRepository.findByProjectId(projectId)
                .stream()
                .map(ProjectAssignment::getEmployeeId)
                .toList();
    }

    /* =========================================================
       MAPPER
       ========================================================= */
    private ProjectResponse mapToResponse(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .client(project.getClient())
                .budget(project.getBudget())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .status(project.getStatus())
                .build();
    }
    /* =========================================================
   GET EMPLOYEE PROJECTS
   ========================================================= */
    public List<EmployeeProjectResponse> getEmployeeProjects(Long employeeId) {

        return assignmentRepository.findByEmployeeId(employeeId)
                .stream()
                .map(assignment -> {

                    Project project = projectRepository.findById(assignment.getProjectId())
                            .orElseThrow(() -> new RuntimeException("Project not found"));

                    return EmployeeProjectResponse.builder()
                            .projectId(project.getId())
                            .projectName(project.getName())
                            .status(project.getStatus())
                            .build();
                })
                .toList();
    }
    /* =========================================================
   PROJECT P&L
   ========================================================= */
    public ProjectPLResponse getProjectPL(Long projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        List<Timesheet> timesheets = timesheetRepository.findByProjectId(projectId);

        double totalHours = timesheets.stream()
                .mapToDouble(Timesheet::getHours)
                .sum();

        double hourlyRate = 1000; // you can make dynamic later

        double actualCost = totalHours * hourlyRate;

        double profitOrLoss = project.getBudget() - actualCost;

        return ProjectPLResponse.builder()
                .projectId(projectId)
                .budget(project.getBudget())
                .actualCost(actualCost)
                .profitOrLoss(profitOrLoss)
                .build();
    }
    /* =========================================================
   RESOURCE UTILIZATION
   ========================================================= */
    public List<ResourceUtilizationResponse> getResourceUtilization(Long projectId) {

        List<Timesheet> timesheets = timesheetRepository.findByProjectId(projectId);

        Map<Long, Double> hoursPerEmployee = timesheets.stream()
                .collect(Collectors.groupingBy(
                        Timesheet::getEmployeeId,
                        Collectors.summingDouble(Timesheet::getHours)
                ));

        return hoursPerEmployee.entrySet().stream()
                .map(entry -> {

                    double hours = entry.getValue();

                    String status;
                    if (hours > 8) {
                        status = "OVERUTILIZED";
                    } else if (hours < 6) {
                        status = "UNDERUTILIZED";
                    } else {
                        status = "NORMAL";
                    }

                    return ResourceUtilizationResponse.builder()
                            .employeeId(entry.getKey())
                            .totalHours(hours)
                            .status(status)
                            .build();
                })
                .toList();
    }
    /* =========================================================
   PROJECT STATUS REPORT
   ========================================================= */
    public ProjectStatusReportResponse getStatusReport(Long projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        List<Milestone> milestones = milestoneRepository.findByProjectId(projectId);
        List<Timesheet> timesheets = timesheetRepository.findByProjectId(projectId);

        int totalMilestones = milestones.size();

        int completedMilestones = (int) milestones.stream()
                .filter(m -> "ON_TRACK".equalsIgnoreCase(m.getStatus()))
                .count();

        int delayedMilestones = (int) milestones.stream()
                .filter(m -> "DELAYED".equalsIgnoreCase(m.getStatus()))
                .count();

        double totalHours = timesheets.stream()
                .mapToDouble(Timesheet::getHours)
                .sum();

        return ProjectStatusReportResponse.builder()
                .projectId(project.getId())
                .projectName(project.getName())
                .status(project.getStatus())
                .totalMilestones(totalMilestones)
                .completedMilestones(completedMilestones)
                .delayedMilestones(delayedMilestones)
                .totalHours(totalHours)
                .build();
    }
}
