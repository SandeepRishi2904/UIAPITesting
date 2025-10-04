package com.testframework.util;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Component
public class ArtifactManager {

    private static final Logger log = LoggerFactory.getLogger(ArtifactManager.class);

    @Value("${artifacts.path}")
    private String artifactsPath;

    /**
     * Captures a screenshot and saves it to a unique path.
     * @return The relative path to the artifact FOLDER.
     */
    public String captureScreenshot(WebDriver driver, Long executionId, String testCaseName) {
        String relativeFolderPath = Paths.get(String.valueOf(executionId), testCaseName).toString();
        Path screenshotPath = Paths.get(artifactsPath, relativeFolderPath, "failure-screenshot.png");

        log.info("Attempting to save screenshot to path: {}", screenshotPath);
        try {
            Files.createDirectories(screenshotPath.getParent());
            File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(screenshotFile.toPath(), screenshotPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("Screenshot saved successfully.");
            return relativeFolderPath;
        } catch (Exception e) {
            log.error("Failed to save screenshot for TestCase: {}", testCaseName, e);
            return null;
        }
    }

    /**
     * Saves API request and response logs to files.
     * @return The relative path to the artifact FOLDER.
     */
    public String saveApiLogs(Long executionId, String testCaseName, String logContent) {
        String relativeFolderPath = Paths.get(String.valueOf(executionId), testCaseName).toString();
        Path fullFolderPath = Paths.get(artifactsPath, relativeFolderPath);

        log.info("Attempting to save API logs to path: {}", fullFolderPath);
        try {
            Files.createDirectories(fullFolderPath);

            // Split the combined log into request and response parts
            int responseStartIndex = logContent.indexOf("HTTP/");
            String requestLog = responseStartIndex != -1 ? logContent.substring(0, responseStartIndex) : logContent;
            String responseLog = responseStartIndex != -1 ? logContent.substring(responseStartIndex) : "";

            if (!requestLog.isBlank()) {
                try (FileWriter writer = new FileWriter(fullFolderPath.resolve("request.log").toFile())) {
                    writer.write(requestLog);
                }
            }
            if (!responseLog.isBlank()) {
                try (FileWriter writer = new FileWriter(fullFolderPath.resolve("response.log").toFile())) {
                    writer.write(responseLog);
                }
            }
            log.info("API logs saved successfully.");
            return relativeFolderPath;
        } catch (IOException e) {
            log.error("Failed to save API logs for TestCase: {}", testCaseName, e);
            return null;
        }
    }
}