package com.web_hub.web_hub.hr.performance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PerformanceService {

    private final PerformanceReviewRepository repository;

    public PerformanceResponse createReview(CreatePerformanceRequest request) {

        PerformanceReview review = PerformanceReview.builder()
                .employeeId(request.getEmployeeId())
                .reviewPeriod(request.getReviewPeriod())
                .rating(request.getRating())
                .comments(request.getComments())
                .status("OPEN")
                .createdAt(LocalDateTime.now())
                .build();

        PerformanceReview saved = repository.save(review);

        return mapToResponse(saved);
    }

    public List<PerformanceResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<PerformanceResponse> getByEmployee(Long employeeId) {
        return repository.findByEmployeeId(employeeId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private PerformanceResponse mapToResponse(PerformanceReview review) {
        return PerformanceResponse.builder()
                .id(review.getId())
                .employeeId(review.getEmployeeId())
                .reviewPeriod(review.getReviewPeriod())
                .rating(review.getRating())
                .comments(review.getComments())
                .status(review.getStatus())
                .build();
    }
}
