package com.web_hub.web_hub.hr.Employees;

import com.web_hub.web_hub.hr.issuewarnings.EmployeeRecord;
import com.web_hub.web_hub.hr.issuewarnings.RecordType;
import com.web_hub.web_hub.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    long countByStatus(String status);
    Optional<Employee> findByEmail(String email);
    Optional<Employee> findByUser(User user);
    boolean existsByUser(User user);



}
