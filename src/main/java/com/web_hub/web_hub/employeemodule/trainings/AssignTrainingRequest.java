package com.web_hub.web_hub.employeemodule.trainings;

import lombok.Data;

@Data
public class AssignTrainingRequest {
    private Long employeeId;
    private Long trainingId;
}
