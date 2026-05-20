package com.web_hub.web_hub.employeemodule.trainings;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainingAssignmentRepository extends JpaRepository<TrainingAssignment, Long> {

    List<TrainingAssignment> findByEmployeeId(Long employeeId);
}
