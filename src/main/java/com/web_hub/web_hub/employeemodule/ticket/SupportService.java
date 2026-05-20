package com.web_hub.web_hub.employeemodule.ticket;

import com.web_hub.web_hub.employeemodule.dto.TicketRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupportService {

    private final SupportTicketRepository supportTicketRepository;

    public void createTicket(TicketRequest req) {

        SupportTicket ticket = SupportTicket.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .priority(req.getPriority())
                .status("OPEN")
                .build();

        supportTicketRepository.save(ticket);
    }

    public List<SupportTicket> getAllTickets() {
        return supportTicketRepository.findAll();
    }
    public SupportTicket updateStatus(Long id, String status) {

        SupportTicket ticket = supportTicketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        ticket.setStatus(status);

        return supportTicketRepository.save(ticket);
    }
}
