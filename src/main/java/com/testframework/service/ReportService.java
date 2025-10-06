package com.testframework.service;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.markuputils.CodeLanguage;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.testframework.model.TestExecution;
import com.testframework.model.TestResult;
import com.testframework.model.TestStatus;
import com.testframework.repository.TestExecutionRepository;
import com.testframework.repository.TestResultRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final TestExecutionRepository executionRepository;
    private final TestResultRepository resultRepository;

    @Value("${test-framework.reports.directory}")
    private String reportsBaseDir;

    @Value("${artifacts.path}")
    private String artifactsBaseDir;

    public ReportService(TestExecutionRepository executionRepository, TestResultRepository resultRepository) {
        this.executionRepository = executionRepository;
        this.resultRepository = resultRepository;
    }

    public Resource generateReport(Long executionId, String format) throws Exception {
        TestExecution execution = executionRepository.findByIdWithAllDetails(executionId)
                .orElseThrow(() -> new IllegalArgumentException("Execution not found: " + executionId));

        Path reportDir = Paths.get(reportsBaseDir, String.valueOf(executionId));
        Files.createDirectories(reportDir);

        Path reportPath;
        switch (format.toLowerCase()) {
            case "html":
                reportPath = generateHtmlReport(execution, reportDir);
                break;
            case "csv":
                reportPath = generateCsvReport(execution, reportDir);
                break;
            case "junit":
                reportPath = generateJUnitXmlReport(execution, reportDir);
                break;
            default:
                throw new IllegalArgumentException("Unsupported report format: " + format);
        }

        return new FileSystemResource(reportPath);
    }

    private Path generateHtmlReport(TestExecution execution, Path reportDir) {
        Path htmlReportPath = reportDir.resolve("report.html");
        ExtentReports extent = new ExtentReports();
        ExtentSparkReporter spark = new ExtentSparkReporter(htmlReportPath.toFile());
        extent.attachReporter(spark);

        spark.config().setDocumentTitle("Test Automation Report - Execution " + execution.getId());
        spark.config().setReportName("Test Suite: " + execution.getTestSuite().getName());

        for (TestResult result : execution.getTestResults()) {
            ExtentTest test = extent.createTest(result.getTestCaseName());
            switch (result.getStatus()) {
                case PASSED:
                    test.pass("Test Passed");
                    break;
                case FAILED:
                    test.fail(MarkupHelper.createCodeBlock(result.getErrorMessage()));

                    if (result.getArtifactPath() != null) {
                        Path artifactFolder = Paths.get(artifactsBaseDir, result.getArtifactPath());

                        Path screenshotPath = artifactFolder.resolve("failure-screenshot.png");
                        if (Files.exists(screenshotPath)) {
                            try {
                                byte[] imageBytes = FileUtils.readFileToByteArray(screenshotPath.toFile());
                                String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                                test.fail("Screenshot:", MediaEntityBuilder.createScreenCaptureFromBase64String(base64Image, "Failure Screenshot").build());
                            } catch (IOException e) {
                                test.fail("Could not attach screenshot file.");
                            }
                        }

                        Path requestLogPath = artifactFolder.resolve("request.log");
                        if (Files.exists(requestLogPath)) {
                            try {
                                String requestLog = Files.readString(requestLogPath);
                                test.info("<b>API Request</b>");
                                test.info(MarkupHelper.createCodeBlock(requestLog, CodeLanguage.JSON));
                            } catch (IOException e) {
                                test.info("Could not read request.log");
                            }
                        }
                        Path responseLogPath = artifactFolder.resolve("response.log");
                        if (Files.exists(responseLogPath)) {
                            try {
                                String responseLog = Files.readString(responseLogPath);
                                test.info("<b>API Response</b>");
                                test.info(MarkupHelper.createCodeBlock(responseLog, CodeLanguage.JSON));
                            } catch (IOException e) {
                                test.info("Could not read response.log");
                            }
                        }
                    }
                    break;
                case SKIPPED:
                    test.skip("Test Skipped");
                    break;
            }
        }
        extent.flush();
        return htmlReportPath;
    }

    // --- THIS IS THE NEW AND IMPROVED CSV METHOD ---
    private Path generateCsvReport(TestExecution execution, Path reportDir) throws IOException {
        Path csvReportPath = reportDir.resolve("report.csv");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try (FileWriter out = new FileWriter(csvReportPath.toFile());
             CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT)) {

            // --- 1. Add Summary Header ---
            printer.printRecord("Execution ID:", execution.getId());
            printer.printRecord("Suite Name:", execution.getTestSuite().getName());
            printer.printRecord("Status:", execution.getStatus());
            printer.printRecord("Start Time:", execution.getStartedAt().format(formatter));
            printer.printRecord("End Time:", execution.getEndedAt() != null ? execution.getEndedAt().format(formatter) : "N/A");
            printer.printRecord("Total Tests:", execution.getTotalTests());
            printer.printRecord("Passed:", execution.getPassedTests());
            printer.printRecord("Failed:", execution.getFailedTests());
            printer.printRecord(""); // Blank line for spacing

            // --- 2. Add Detailed Headers for the Results ---
            printer.printRecord("Test Case ID", "Test Case Name", "Status", "Duration (ms)", "Executed At", "Error Message", "Artifact Path");

            // --- 3. Add the Data for Each Test Result ---
            for (TestResult result : execution.getTestResults()) {
                printer.printRecord(
                        result.getTestCase() != null ? result.getTestCase().getId() : "N/A",
                        result.getTestCaseName(),
                        result.getStatus(),
                        result.getExecutionTimeMs(),
                        result.getExecutedAt().format(formatter),
                        result.getErrorMessage(),
                        result.getArtifactPath()
                );
            }
        }
        return csvReportPath;
    }

    private Path generateJUnitXmlReport(TestExecution execution, Path reportDir) throws Exception {
        Path xmlReportPath = reportDir.resolve("junit-report.xml");

        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element testsuite = doc.createElement("testsuite");
        testsuite.setAttribute("name", execution.getTestSuite().getName());
        testsuite.setAttribute("tests", String.valueOf(execution.getTotalTests()));
        testsuite.setAttribute("failures", String.valueOf(execution.getFailedTests()));
        testsuite.setAttribute("skipped", String.valueOf(execution.getSkippedTests()));
        doc.appendChild(testsuite);

        for(TestResult result : execution.getTestResults()) {
            Element testcase = doc.createElement("testcase");
            testcase.setAttribute("name", result.getTestCaseName());
            testcase.setAttribute("time", String.valueOf(result.getExecutionTimeMs() / 1000.0));

            if(result.getStatus() == TestStatus.FAILED) {
                Element failure = doc.createElement("failure");
                failure.setAttribute("message", result.getErrorMessage());
                testcase.appendChild(failure);
            }
            if(result.getStatus() == TestStatus.SKIPPED) {
                Element skipped = doc.createElement("skipped");
                testcase.appendChild(skipped);
            }
            testsuite.appendChild(testcase);
        }

        TransformerFactory.newInstance().newTransformer().transform(new DOMSource(doc), new StreamResult(xmlReportPath.toFile()));
        return xmlReportPath;
    }
    public Map<String, Object> getExecutionSummary(Long executionId) {
        TestExecution execution = executionRepository.findById(executionId)
                .orElseThrow(() -> new IllegalArgumentException("Execution not found: " + executionId));
        Map<String, Object> summary = new HashMap<>();
        summary.put("executionId", execution.getId());
        summary.put("suiteName", execution.getTestSuite().getName());
        summary.put("status", execution.getStatus());
        summary.put("totalTests", execution.getTotalTests());
        summary.put("passedTests", execution.getPassedTests());
        summary.put("failedTests", execution.getFailedTests());
        summary.put("skippedTests", execution.getSkippedTests());
        summary.put("startedAt", execution.getStartedAt());
        summary.put("endedAt", execution.getEndedAt());
        return summary;
    }

    public Map<String, Object> collectExecutionLogs(Long executionId) {
        TestExecution execution = executionRepository.findByIdWithAllDetails(executionId)
                .orElseThrow(() -> new IllegalArgumentException("Execution not found: " + executionId));

        Map<String, Object> collectedLogs = new LinkedHashMap<>();
        collectedLogs.put("executionId", executionId);

        Map<String, Map<String, String>> testLogs = execution.getTestResults().stream()
                .filter(result -> result.getStatus() == TestStatus.FAILED && result.getArtifactPath() != null)
                .collect(Collectors.toMap(
                        TestResult::getTestCaseName,
                        result -> {
                            Map<String, String> logs = new HashMap<>();
                            try {
                                Path requestLogPath = Paths.get(artifactsBaseDir, result.getArtifactPath(), "request.log");
                                if (Files.exists(requestLogPath)) {
                                    logs.put("request", Files.readString(requestLogPath));
                                }
                                Path responseLogPath = Paths.get(artifactsBaseDir, result.getArtifactPath(), "response.log");
                                if (Files.exists(responseLogPath)) {
                                    logs.put("response", Files.readString(responseLogPath));
                                }
                            } catch (IOException e) {
                                logs.put("error", "Failed to read log files: " + e.getMessage());
                            }
                            return logs;
                        }
                ));

        collectedLogs.put("failedTestLogs", testLogs);
        return collectedLogs;
    }

    public Resource getScreenshot(Long resultId) {
        Optional<TestResult> resultOpt = resultRepository.findById(resultId);
        if (resultOpt.isPresent()) {
            TestResult result = resultOpt.get();
            if (result.getArtifactPath() != null && !result.getArtifactPath().isEmpty()) {
                try {
                    Path screenshotPath = Paths.get(artifactsBaseDir, result.getArtifactPath(), "failure-screenshot.png");
                    if (Files.exists(screenshotPath)) {
                        return new FileSystemResource(screenshotPath.toFile());
                    }
                } catch (Exception e) {
                    return null;
                }
            }
        }
        return null;
    }
}