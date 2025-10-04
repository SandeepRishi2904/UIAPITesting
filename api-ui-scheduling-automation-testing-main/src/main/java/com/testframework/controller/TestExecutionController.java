package com.testframework.controller;

import com.testframework.model.TestExecution;
import com.testframework.service.TestExecutionService;
import com.testframework.service.TestRunnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/schedule")
@CrossOrigin(origins = "*")
public class TestExecutionController {

    private final TestExecutionService testExecutionService;
    private final TestRunnerService testRunnerService;

    @Autowired
    public TestExecutionController(TestExecutionService testExecutionService, TestRunnerService testRunnerService) {
        this.testExecutionService = testExecutionService;
        this.testRunnerService = testRunnerService;
    }

    @PostMapping("/run")
    public ResponseEntity<TestExecution> scheduleTestRun(@RequestParam Long suiteId) {
        // Step 1: Create the execution record. This transaction will complete first.
        TestExecution execution = testExecutionService.scheduleTestRun(suiteId);

        // Step 2: Now that the record is saved, trigger the background task.
        testRunnerService.runTestSuite(execution.getId());

        return ResponseEntity.ok(execution);
    }

    @GetMapping("/execution/status/{executionId}")
    public ResponseEntity<TestExecution> getExecutionStatus(@PathVariable Long executionId) {
        Optional<TestExecution> execution = testExecutionService.getExecutionById(executionId);
        return execution.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/executions")
    public ResponseEntity<Page<TestExecution>> getAllExecutions(Pageable pageable) {
        Page<TestExecution> executions = testExecutionService.getAllExecutions(pageable);
        return ResponseEntity.ok(executions);
    }

    @GetMapping("/executions/suite/{suiteId}")
    public ResponseEntity<Set<TestExecution>> getExecutionsBySuite(@PathVariable Long suiteId) {
        Set<TestExecution> executions = testExecutionService.getExecutionsBySuite(suiteId);
        return ResponseEntity.ok(executions);
    }

    @PostMapping("/cancel/{executionId}")
    public ResponseEntity<TestExecution> cancelExecution(@PathVariable Long executionId) {
        Optional<TestExecution> cancelled = testExecutionService.cancelExecution(executionId);
        return cancelled.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/latest/{suiteId}")
    public ResponseEntity<TestExecution> getLatestExecution(@PathVariable Long suiteId) {
        Optional<TestExecution> execution = testExecutionService.getLatestExecutionForSuite(suiteId);
        return execution.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}