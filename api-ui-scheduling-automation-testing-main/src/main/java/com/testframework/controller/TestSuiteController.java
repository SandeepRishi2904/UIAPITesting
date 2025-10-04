package com.testframework.controller;

import com.testframework.model.TestSuite;
import com.testframework.service.TestSuiteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * REST Controller for managing test suites.
 */
@RestController
@RequestMapping("/tests/suites")
@CrossOrigin(origins = "*")
public class TestSuiteController {

    private final TestSuiteService testSuiteService;

    @Autowired
    public TestSuiteController(TestSuiteService testSuiteService) {
        this.testSuiteService = testSuiteService;
    }

    /**
     * Create a new test suite.
     * POST /tests/suites
     */
    @PostMapping
    public ResponseEntity<TestSuite> createTestSuite(@Valid @RequestBody TestSuite testSuite) {
        TestSuite createdSuite = testSuiteService.createTestSuite(testSuite);
        return new ResponseEntity<>(createdSuite, HttpStatus.CREATED);
    }

    /**
     * Get all test suites with pagination.
     * GET /tests/suites
     */
    @GetMapping
    public ResponseEntity<Page<TestSuite>> getAllTestSuites(Pageable pageable) {
        Page<TestSuite> testSuites = testSuiteService.getAllTestSuites(pageable);
        return ResponseEntity.ok(testSuites);
    }

    /**
     * Get a specific test suite by ID.
     * GET /tests/suites/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<TestSuite> getTestSuite(@PathVariable Long id) {
        Optional<TestSuite> testSuite = testSuiteService.getTestSuiteById(id);
        return testSuite.map(suite -> ResponseEntity.ok(suite))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update an existing test suite.
     * PUT /tests/suites/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<TestSuite> updateTestSuite(@PathVariable Long id,
                                                     @Valid @RequestBody TestSuite testSuite) {
        Optional<TestSuite> updatedSuite = testSuiteService.updateTestSuite(id, testSuite);
        return updatedSuite.map(suite -> ResponseEntity.ok(suite))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete a test suite.
     * DELETE /tests/suites/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTestSuite(@PathVariable Long id) {
        boolean deleted = testSuiteService.deleteTestSuite(id);
        return deleted ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    /**
     * Get test suite with all test cases.
     * GET /tests/suites/{id}/with-cases
     */
    @GetMapping("/{id}/with-cases")
    public ResponseEntity<TestSuite> getTestSuiteWithCases(@PathVariable Long id) {
        Optional<TestSuite> testSuite = testSuiteService.getTestSuiteWithCases(id);
        return testSuite.map(suite -> ResponseEntity.ok(suite))
                .orElse(ResponseEntity.notFound().build());
    }
}