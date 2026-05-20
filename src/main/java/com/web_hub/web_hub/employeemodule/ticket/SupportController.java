package com.web_hub.web_hub.employeemodule.ticket;

import com.web_hub.web_hub.employeemodule.dto.TicketRequest;
import com.web_hub.web_hub.employeemodule.dto.UpdateTicketStatusRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/support")
@RequiredArgsConstructor
public class SupportController {

    private final SupportService supportService;

    @PostMapping("/tickets")
    public ResponseEntity<?> createTicket(@RequestBody TicketRequest req) {
        supportService.createTicket(req);
        return ResponseEntity.ok("Ticket created");
    }
    @GetMapping("/all")
    public List<SupportTicket> getAllTickets() {
        return supportService.getAllTickets();
    }
    @PutMapping("/tickets/{id}/status")
    public SupportTicket updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateTicketStatusRequest request
    ) {
        return supportService.updateStatus(id, request.getStatus());
    }
}
