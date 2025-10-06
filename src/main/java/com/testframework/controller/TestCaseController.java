package com.testframework.controller;

import com.testframework.model.TestCase;
import com.testframework.service.TestCaseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST Controller for managing test cases.
 */
@RestController
@RequestMapping("/tests/cases")
@CrossOrigin(origins = "*")
public class TestCaseController {

    private final TestCaseService testCaseService;

    @Autowired
    public TestCaseController(TestCaseService testCaseService) {
        this.testCaseService = testCaseService;
    }

    /**
     * Create a new test case.
     * POST /tests/cases
     */
    @PostMapping
    public ResponseEntity<TestCase> createTestCase(@Valid @RequestBody TestCase testCase) {
        TestCase createdCase = testCaseService.createTestCase(testCase);
        return new ResponseEntity<>(createdCase, HttpStatus.CREATED);
    }

    /**
     * Get all test cases with pagination.
     * GET /tests/cases
     */
    @GetMapping
    public ResponseEntity<Page<TestCase>> getAllTestCases(Pageable pageable) {
        Page<TestCase> testCases = testCaseService.getAllTestCases(pageable);
        return ResponseEntity.ok(testCases);
    }

    /**
     * Get a specific test case by ID.
     * GET /tests/cases/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<TestCase> getTestCase(@PathVariable Long id) {
        Optional<TestCase> testCase = testCaseService.getTestCaseById(id);
        return testCase.map(test -> ResponseEntity.ok(test))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update an existing test case.
     * PUT /tests/cases/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<TestCase> updateTestCase(@PathVariable Long id,
                                                   @Valid @RequestBody TestCase testCase) {
        Optional<TestCase> updatedCase = testCaseService.updateTestCase(id, testCase);
        return updatedCase.map(test -> ResponseEntity.ok(test))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete a test case.
     * DELETE /tests/cases/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTestCase(@PathVariable Long id) {
        boolean deleted = testCaseService.deleteTestCase(id);
        return deleted ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    /**
     * Get test cases by suite ID.
     * GET /tests/cases/suite/{suiteId}
     */
    @GetMapping("/suite/{suiteId}")
    public ResponseEntity<List<TestCase>> getTestCasesBySuite(@PathVariable Long suiteId) {
        List<TestCase> testCases = testCaseService.getTestCasesBySuite(suiteId);
        return ResponseEntity.ok(testCases);
    }

    /**
     * Integration endpoint: Add test case to existing suite.
     * POST /tests/integrate - as specified in the requirements
     */
    @PostMapping("/integrate")
    public ResponseEntity<TestCase> integrateTestCase(@Valid @RequestBody TestCase testCase) {
        TestCase integratedCase = testCaseService.createTestCase(testCase);
        return new ResponseEntity<>(integratedCase, HttpStatus.CREATED);
    }
}