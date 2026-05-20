package com.web_hub.web_hub.employeemodule.trainings;
import com.web_hub.web_hub.employeemodule.dto.TrainingRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class TrainingService {

    private final TrainingRepository trainingRepository;
    private final TrainingAssignmentRepository assignmentRepository;

    public Training createTraining(TrainingRequest req) {

        Training training = Training.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .provider(req.getProvider())
                .build();

        return trainingRepository.save(training);
    }

    public void assignTraining(AssignTrainingRequest req) {

        TrainingAssignment assignment = TrainingAssignment.builder()
                .employeeId(req.getEmployeeId())
                .trainingId(req.getTrainingId())
                .status("ASSIGNED")
                .build();

        assignmentRepository.save(assignment);
    }

    public List<Training> getMyTrainings(Long employeeId) {

        List<TrainingAssignment> assignments =
                assignmentRepository.findByEmployeeId(employeeId);

        return assignments.stream()
                .map(a -> trainingRepository.findById(a.getTrainingId())
                        .orElseThrow(() -> new RuntimeException("Training not found")))
                .toList();
    }

    public void markCompleted(Long assignmentId) {

        TrainingAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        assignment.setStatus("COMPLETED");

        assignmentRepository.save(assignment);
    }
}
