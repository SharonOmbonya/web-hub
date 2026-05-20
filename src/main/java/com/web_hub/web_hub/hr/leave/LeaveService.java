package com.web_hub.web_hub.hr.leave;



import com.web_hub.web_hub.employeemodule.dto.LeaveRequest;
import com.web_hub.web_hub.employeemodule.dto.LeaveResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveService {

    private final LeaveRepository repository;

    /* ================= APPLY ================= */

    public LeaveResponse apply(LeaveRequest req) {

        Leave leave = new Leave();
        leave.setType(req.getType());
        leave.setStartDate(req.getStartDate());
        leave.setEndDate(req.getEndDate());
        leave.setStatus("PENDING");

        repository.save(leave);

        return map(leave);
    }

    /* ================= MY LEAVES ================= */

    public List<LeaveResponse> getMyLeaves() {
        return repository.findAll() // later filter by logged-in user
                .stream()
                .map(this::map)
                .toList();
    }

    /* ================= GET ALL ================= */

    public List<LeaveResponse> getAllLeaves() {
        return repository.findAll()
                .stream()
                .map(this::map)
                .toList();
    }

    /* ================= APPROVE ================= */

    public LeaveResponse approveLeave(Long id) {

        Leave leave = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave not found"));

        leave.setStatus("APPROVED");

        repository.save(leave);

        return map(leave);
    }

    /* ================= REJECT ================= */

    public LeaveResponse rejectLeave(Long id) {

        Leave leave = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave not found"));

        leave.setStatus("REJECTED");

        repository.save(leave);

        return map(leave);
    }

    /* ================= GET BY ID ================= */

    public LeaveResponse getById(Long id) {

        Leave leave = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave not found"));

        return map(leave);
    }

    /* ================= MAPPER ================= */

    private LeaveResponse map(Leave l) {
        return new LeaveResponse(
                l.getId(),
                l.getType(),
                l.getStartDate(),
                l.getEndDate(),
                l.getStatus(),
                l.getReason()
        );
    }
}
