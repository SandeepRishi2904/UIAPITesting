package com.testframework.service;

import com.testframework.repository.TestExecutionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final TestExecutionRepository executionRepository;

    public AnalyticsService(TestExecutionRepository executionRepository) {
        this.executionRepository = executionRepository;
    }

    public List<Map<String, Object>> getExecutionTrends(Long suiteId, int days) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(days);

        return executionRepository.findByTestSuiteIdAndStartedAtBetween(suiteId, startDate, endDate)
                .stream()
                .map(exec -> Map.of(
                        "executionId", (Object)exec.getId(),
                        "date", (Object)exec.getStartedAt().toLocalDate(),
                        "passed", (Object)exec.getPassedTests(),
                        "failed", (Object)exec.getFailedTests(),
                        "skipped", (Object)exec.getSkippedTests()
                ))
                .collect(Collectors.toList());
    }
}