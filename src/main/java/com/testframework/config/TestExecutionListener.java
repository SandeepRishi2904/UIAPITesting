package com.testframework.config;

import com.testframework.model.*;
import com.testframework.repository.TestCaseRepository;
import com.testframework.repository.TestExecutionRepository;
import com.testframework.repository.TestResultRepository;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Scope("prototype")
public class TestExecutionListener extends TestListenerAdapter {

    private Long executionId;
    private TestExecution execution;
    private final Map<String, TestResult> testResultsMap = new ConcurrentHashMap<>();

    @Autowired
    private TestExecutionRepository executionRepository;
    @Autowired
    private TestResultRepository resultRepository;
    @Autowired
    private TestCaseRepository testCaseRepository;

    @Value("${test-framework.artifacts.directory}")
    private String artifactsBaseDir;

    public void setExecutionId(Long executionId) {
        this.executionId = executionId;
    }

    @Override
    public void onStart(ITestContext context) {
        super.onStart(context);
        this.execution = executionRepository.findById(executionId)
                .orElseThrow(() -> new IllegalStateException("Execution not found at suite start"));
        execution.setTotalTests(context.getAllTestMethods().length);
        executionRepository.save(execution);

        // This is the correct place to set the executionId for the tests to access.
        context.getSuite().setAttribute("executionId", this.executionId);
    }

    @Override
    public void onTestStart(ITestResult result) {
        super.onTestStart(result);
        TestResult testResult = new TestResult();
        testResult.setTestExecution(execution);

        String testId = result.getName();
        testResult.setTestCaseName(testId);

        Optional<TestCase> existingTestCase = testCaseRepository.findByName(testId);
        TestCase testCaseToLink;
        if (existingTestCase.isPresent()) {
            testCaseToLink = existingTestCase.get();
        } else {
            TestCase newTestCase = new TestCase();
            newTestCase.setName(testId);
            newTestCase.setDescription("Auto-generated from test run.");
            newTestCase.setTestSuite(execution.getTestSuite());
            newTestCase.setTestType(execution.getTestSuite().getTestType());
            newTestCase.setPriority(Priority.MEDIUM);
            testCaseToLink = testCaseRepository.save(newTestCase);
        }
        testResult.setTestCase(testCaseToLink);

        testResult.setStatus(TestStatus.RUNNING);
        testResult.setExecutedAt(LocalDateTime.now());
        TestResult savedResult = resultRepository.save(testResult);
        testResultsMap.put(testId, savedResult);
    }

    @Override
    public void onTestSuccess(ITestResult tr) {
        super.onTestSuccess(tr);
        updateTestResult(tr, TestStatus.PASSED, null);
    }

    @Override
    public void onTestFailure(ITestResult tr) {
        super.onTestFailure(tr);
        String errorMessage = tr.getThrowable() != null ? tr.getThrowable().toString() : "Unknown failure";
        updateTestResult(tr, TestStatus.FAILED, errorMessage);
        saveArtifacts(tr); // Correctly call saveArtifacts on failure.
    }

    @Override
    public void onTestSkipped(ITestResult tr) {
        super.onTestSkipped(tr);
        updateTestResult(tr, TestStatus.SKIPPED, "Test skipped");
    }

    @Override
    public void onFinish(ITestContext context) {
        super.onFinish(context);
        TestExecution finalExecution = executionRepository.findById(executionId).orElseThrow();
        finalExecution.setPassedTests(context.getPassedTests().size());
        finalExecution.setFailedTests(context.getFailedTests().size());
        finalExecution.setSkippedTests(context.getSkippedTests().size());
        finalExecution.setStatus(ExecutionStatus.COMPLETED);
        finalExecution.setEndedAt(LocalDateTime.now());
        executionRepository.save(finalExecution);
    }

    private void updateTestResult(ITestResult tr, TestStatus status, String errorMessage) {
        TestResult testResult = testResultsMap.get(tr.getName());
        if (testResult != null) {
            testResult.setStatus(status);
            testResult.setExecutionTimeMs(tr.getEndMillis() - tr.getStartMillis());

            if (errorMessage != null) {
                testResult.setErrorMessage(errorMessage);
            }

            resultRepository.save(testResult);
        }
    }

    private void saveArtifacts(ITestResult tr) {
        TestResult testResult = testResultsMap.get(tr.getName());
        if (testResult == null) return;

        try {
            Path artifactPath = Paths.get(artifactsBaseDir, String.valueOf(executionId), testResult.getTestCaseName());
            Files.createDirectories(artifactPath);

            Object screenshotFileObj = tr.getAttribute("screenshotFile");
            if (screenshotFileObj instanceof File) {
                File screenshotFile = (File) screenshotFileObj;
                File destFile = new File(artifactPath.resolve("failure-screenshot.png").toString());
                FileUtils.copyFile(screenshotFile, destFile);

                // Set the path on the result and save it to the database
                testResult.setArtifactPath(artifactPath.toString());
                resultRepository.save(testResult);
            }

            Object logContentObj = tr.getTestContext().getAttribute("apiLogContent");
            if (logContentObj instanceof String) {
                // ... API log saving logic ...
            }

        } catch (IOException e) {
            System.err.println("Failed to save artifacts: " + e.getMessage());
            e.printStackTrace();
        }
    }
}