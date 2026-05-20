package com.web_hub.web_hub.projects.milestones;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MilestoneService {

    private final MilestoneRepository milestoneRepository;

    /* =========================================================
       CREATE MILESTONE
       ========================================================= */
    public MilestoneResponse create(CreateMilestoneRequest request) {

        Milestone milestone = Milestone.builder()
                .projectId(request.getProjectId())
                .name(request.getName())
                .status(request.getStatus())
                .dueDate(request.getDueDate())
                .build();

        milestoneRepository.save(milestone);

        return mapToResponse(milestone);
    }

    /* =========================================================
       GET PROJECT MILESTONES
       ========================================================= */
    public List<MilestoneResponse> getByProject(Long projectId) {
        return milestoneRepository.findByProjectId(projectId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /* =========================================================
       UPDATE STATUS
       ========================================================= */
    public MilestoneResponse updateStatus(Long id, String status) {

        Milestone milestone = milestoneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Milestone not found"));

        milestone.setStatus(status);

        milestoneRepository.save(milestone);

        return mapToResponse(milestone);
    }

    /* =========================================================
       MAPPER
       ========================================================= */
    private MilestoneResponse mapToResponse(Milestone milestone) {
        return MilestoneResponse.builder()
                .id(milestone.getId())
                .projectId(milestone.getProjectId())
                .name(milestone.getName())
                .status(milestone.getStatus())
                .dueDate(milestone.getDueDate())
                .build();
    }
}
