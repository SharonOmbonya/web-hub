package com.web_hub.web_hub.employeemodule.trainings;

import com.web_hub.web_hub.employeemodule.dto.TrainingRequest;
import com.web_hub.web_hub.hr.Employees.Employee;
import com.web_hub.web_hub.hr.Employees.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainings")
@RequiredArgsConstructor
public class TrainingController {

    private final TrainingService trainingService;
    private final EmployeeRepository employeeRepo;

    @PostMapping
    public Training createTraining(@RequestBody TrainingRequest req) {
        return trainingService.createTraining(req);
    }

    @PostMapping("/assign")
    public String assignTraining(@RequestBody AssignTrainingRequest req) {
        trainingService.assignTraining(req);
        return "Training assigned";
    }

    @GetMapping("/my")
    public List<Training> getMyTrainings(Authentication auth) {

        String email = auth.getName(); // logged-in user

        Employee employee = employeeRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        return trainingService.getMyTrainings(employee.getId());
    }

    @PutMapping("/complete/{assignmentId}")
    public String completeTraining(@PathVariable Long assignmentId) {
        trainingService.markCompleted(assignmentId);
        return "Training marked as completed";
    }
}
