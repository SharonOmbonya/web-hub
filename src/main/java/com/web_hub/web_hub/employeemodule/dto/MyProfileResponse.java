package com.web_hub.web_hub.employeemodule.dto;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

public record MyProfileResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        String department,
        String position,
        Double salary,
        String status

) {}
