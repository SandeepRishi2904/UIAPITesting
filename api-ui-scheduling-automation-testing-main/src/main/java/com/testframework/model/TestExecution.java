package com.testframework.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
public class TestExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "test_suite_id")
    @JsonIgnoreProperties("executions")
    @NotNull
    private TestSuite testSuite;

    @Enumerated(EnumType.STRING)
    @NotNull
    private ExecutionStatus status;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    private int totalTests = 0;
    private int passedTests = 0;
    private int failedTests = 0;
    private int skippedTests = 0;

    @OneToMany(mappedBy = "testExecution", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("testExecution")
    private Set<TestResult> testResults = new HashSet<>();

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public TestSuite getTestSuite() { return testSuite; }
    public void setTestSuite(TestSuite testSuite) { this.testSuite = testSuite; }
    public ExecutionStatus getStatus() { return status; }
    public void setStatus(ExecutionStatus status) { this.status = status; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    public LocalDateTime getEndedAt() { return endedAt; }
    public void setEndedAt(LocalDateTime endedAt) { this.endedAt = endedAt; }
    public int getTotalTests() { return totalTests; }
    public void setTotalTests(int totalTests) { this.totalTests = totalTests; }
    public int getPassedTests() { return passedTests; }
    public void setPassedTests(int passedTests) { this.passedTests = passedTests; }
    public int getFailedTests() { return failedTests; }
    public void setFailedTests(int failedTests) { this.failedTests = failedTests; }
    public int getSkippedTests() { return skippedTests; }
    public void setSkippedTests(int skippedTests) { this.skippedTests = skippedTests; }
    public Set<TestResult> getTestResults() { return testResults; }
    public void setTestResults(Set<TestResult> testResults) { this.testResults = testResults; }
}