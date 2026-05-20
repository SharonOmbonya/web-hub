package com.web_hub.web_hub.timesheets;

import com.web_hub.web_hub.projects.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TimesheetService {

    private final TimesheetRepository timesheetRepository;
    private final ProjectRepository projectRepository;

    /* =========================================================
       CREATE TIMESHEET
       ========================================================= */
    public TimesheetResponse create(CreateTimesheetRequest request) {

        projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        Timesheet timesheet = Timesheet.builder()
                .projectId(request.getProjectId())
                .employeeId(request.getEmployeeId())
                .workDate(request.getWorkDate())
                .hours(request.getHours())
                .description(request.getDescription())
                .build();

        timesheetRepository.save(timesheet);

        return mapToResponse(timesheet);
    }

    /* =========================================================
       GET BY PROJECT
       ========================================================= */
    public List<TimesheetResponse> getByProject(Long projectId) {
        return timesheetRepository.findByProjectId(projectId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /* =========================================================
       GET BY EMPLOYEE
       ========================================================= */
    public List<TimesheetResponse> getByEmployee(Long employeeId) {
        return timesheetRepository.findByEmployeeId(employeeId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /* =========================================================
       MAPPER
       ========================================================= */
    private TimesheetResponse mapToResponse(Timesheet t) {
        return TimesheetResponse.builder()
                .id(t.getId())
                .projectId(t.getProjectId())
                .employeeId(t.getEmployeeId())
                .workDate(t.getWorkDate())
                .hours(t.getHours())
                .description(t.getDescription())
                .build();
    }
}
