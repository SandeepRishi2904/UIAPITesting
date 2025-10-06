package com.testframework.service;

import com.testframework.model.ExecutionStatus;
import com.testframework.model.TestExecution;
import com.testframework.model.TestSuite;
import com.testframework.repository.TestExecutionRepository;
import com.testframework.repository.TestSuiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Service
public class TestExecutionService {

    private final TestExecutionRepository testExecutionRepository;
    private final TestSuiteRepository testSuiteRepository;

    @Autowired
    public TestExecutionService(TestExecutionRepository testExecutionRepository, TestSuiteRepository testSuiteRepository) {
        this.testExecutionRepository = testExecutionRepository;
        this.testSuiteRepository = testSuiteRepository;
    }

    @Transactional
    public TestExecution scheduleTestRun(Long suiteId) {
        TestSuite testSuite = testSuiteRepository.findById(suiteId)
                .orElseThrow(() -> new IllegalArgumentException("Test Suite with ID " + suiteId + " not found."));

        TestExecution execution = new TestExecution();
        execution.setTestSuite(testSuite);
        execution.setStatus(ExecutionStatus.QUEUED);
        execution.setStartedAt(LocalDateTime.now());

        return testExecutionRepository.save(execution);
    }

    @Transactional(readOnly = true)
    public Optional<TestExecution> getExecutionById(Long executionId) {
        return testExecutionRepository.findByIdWithAllDetails(executionId);
    }

    @Transactional(readOnly = true)
    public Page<TestExecution> getAllExecutions(Pageable pageable) {
        return testExecutionRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Set<TestExecution> getExecutionsBySuite(Long suiteId) {
        return testExecutionRepository.findByTestSuiteId(suiteId);
    }

    @Transactional(readOnly = true)
    public Optional<TestExecution> getLatestExecutionForSuite(Long suiteId) {
        return testExecutionRepository.findTopByTestSuiteIdOrderByStartedAtDesc(suiteId);
    }

    public Optional<TestExecution> cancelExecution(Long executionId) {
        Optional<TestExecution> executionOpt = testExecutionRepository.findById(executionId);
        if (executionOpt.isPresent()) {
            TestExecution execution = executionOpt.get();
            if (execution.getStatus() == ExecutionStatus.QUEUED || execution.getStatus() == ExecutionStatus.RUNNING) {
                execution.setStatus(ExecutionStatus.CANCELED);
                execution.setEndedAt(LocalDateTime.now());
                return Optional.of(testExecutionRepository.save(execution));
            }
        }
        return Optional.empty();
    }
}