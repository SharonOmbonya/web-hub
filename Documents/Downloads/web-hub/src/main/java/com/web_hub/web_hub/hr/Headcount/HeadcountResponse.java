package com.web_hub.web_hub.hr.Headcount;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HeadcountResponse {

    private long totalEmployees;
    private long activeEmployees;
    private long inactiveEmployees;
}
