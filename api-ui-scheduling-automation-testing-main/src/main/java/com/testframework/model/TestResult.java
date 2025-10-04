package com.testframework.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
public class TestResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_execution_id")
    @JsonIgnoreProperties("testResults")
    @NotNull
    private TestExecution testExecution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_case_id")
    @JsonIgnoreProperties("testResults")
    private TestCase testCase;

    @NotNull
    private String testCaseName;

    @Enumerated(EnumType.STRING)
    @NotNull
    private TestStatus status;

    private LocalDateTime executedAt;
    private long executionTimeMs;

    @Lob
    @Column(columnDefinition="TEXT")
    private String errorMessage;

    private String artifactPath;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public TestExecution getTestExecution() { return testExecution; }
    public void setTestExecution(TestExecution testExecution) { this.testExecution = testExecution; }
    public TestCase getTestCase() { return testCase; }
    public void setTestCase(TestCase testCase) { this.testCase = testCase; }
    public String getTestCaseName() { return testCaseName; }
    public void setTestCaseName(String testCaseName) { this.testCaseName = testCaseName; }
    public TestStatus getStatus() { return status; }
    public void setStatus(TestStatus status) { this.status = status; }
    public LocalDateTime getExecutedAt() { return executedAt; }
    public void setExecutedAt(LocalDateTime executedAt) { this.executedAt = executedAt; }
    public long getExecutionTimeMs() { return executionTimeMs; }
    public void setExecutionTimeMs(long executionTimeMs) { this.executionTimeMs = executionTimeMs; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public String getArtifactPath() { return artifactPath; }
    public void setArtifactPath(String artifactPath) { this.artifactPath = artifactPath; }
}