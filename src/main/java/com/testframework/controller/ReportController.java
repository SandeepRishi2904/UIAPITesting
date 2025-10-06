package com.testframework.controller;

import com.testframework.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller for generating reports and collecting logs.
 */
@RestController
@RequestMapping("/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Generate HTML report for test execution.
     * GET /reports/generate/{executionId}?format=html
     */
    @GetMapping("/generate/{executionId}")
    public ResponseEntity<Resource> generateReport(@PathVariable Long executionId,
                                                   @RequestParam(defaultValue = "html") String format) {
        // --- START OF FIX ---
        // Added a try-catch block to handle the checked exception from the service.
        try {
            Resource report = reportService.generateReport(executionId, format);

            if (report == null) {
                return ResponseEntity.notFound().build();
            }

            String filename = "test-report-" + executionId + "." + format.toLowerCase();
            MediaType mediaType = getMediaTypeForFormat(format);

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(report);
        } catch (Exception e) {
            // If an error occurs during report generation, return a 500 Internal Server Error.
            e.printStackTrace(); // Log the exception for debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        // --- END OF FIX ---
    }

    /**
     * Collect logs for a test execution.
     * POST /logs/collect/{executionId}
     */
    @PostMapping("/logs/collect/{executionId}")
    public ResponseEntity<Map<String, Object>> collectLogs(@PathVariable Long executionId) {
        Map<String, Object> logs = reportService.collectExecutionLogs(executionId);
        return ResponseEntity.ok(logs);
    }

    /**
     * Get report summary for test execution.
     * GET /reports/summary/{executionId}
     */
    @GetMapping("/summary/{executionId}")
    public ResponseEntity<Map<String, Object>> getReportSummary(@PathVariable Long executionId) {
        Map<String, Object> summary = reportService.getExecutionSummary(executionId);
        return summary.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(summary);
    }

    /**
     * Download screenshot for failed test.
     * GET /reports/screenshot/{resultId}
     */
    @GetMapping("/screenshot/{resultId}")
    public ResponseEntity<Resource> getScreenshot(@PathVariable Long resultId) {
        Resource screenshot = reportService.getScreenshot(resultId);

        if (screenshot == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"screenshot-" + resultId + ".png\"")
                .body(screenshot);
    }

    /**
     * Get media type based on report format.
     */
    private MediaType getMediaTypeForFormat(String format) {
        return switch (format.toLowerCase()) {
            case "html" -> MediaType.TEXT_HTML;
            case "csv" -> MediaType.parseMediaType("text/csv");
            case "xml", "junit" -> MediaType.APPLICATION_XML;
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }
}