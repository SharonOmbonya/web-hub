package com.web_hub.web_hub.hr.leave;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LeaveRepository extends JpaRepository<Leave, Long> {

    List<Leave> findByStatus(String status);

    List<Leave> findByEmployeeId(Long employeeId);

    List<Leave> findByEmployeeIdAndStatus(Long employeeId, String status);
}
