package com.web_hub.web_hub.hr.issuewarnings;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRecordRepository extends JpaRepository<EmployeeRecord, Long> {

    List<EmployeeRecord> findByEmployeeId(Long employeeId);
    List<EmployeeRecord> findByType(RecordType type);
}
