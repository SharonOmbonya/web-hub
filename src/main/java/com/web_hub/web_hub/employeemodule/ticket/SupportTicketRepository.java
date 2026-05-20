package com.web_hub.web_hub.employeemodule.ticket;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {

    List<SupportTicket> findByEmployeeId(Long employeeId);
}
