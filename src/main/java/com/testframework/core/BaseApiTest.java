package com.testframework.core;

import com.testframework.util.ArtifactManager;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@SpringBootTest
public abstract class BaseApiTest extends AbstractTestNGSpringContextTests {

    @Autowired
    protected ArtifactManager artifactManager;

    protected ByteArrayOutputStream requestResponseLog;

    @BeforeMethod(alwaysRun = true)
    public void setup() {
        RestAssured.baseURI = "https://reqres.in/api";
        requestResponseLog = new ByteArrayOutputStream();
        try {
            PrintStream logStream = new PrintStream(requestResponseLog, true, StandardCharsets.UTF_8.name());
            RestAssured.filters(new RequestLoggingFilter(logStream), new ResponseLoggingFilter(logStream));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @AfterMethod(alwaysRun = true)
    public void captureLogs(ITestResult result) {
        if (result.getThrowable() != null) {
            long executionId = (Long) result.getTestContext().getSuite().getAttribute("executionId");

            // --- FIX: Use result.getName() ---
            String testCaseName = result.getName();
            String logContent = requestResponseLog.toString(StandardCharsets.UTF_8);

            String artifactPath = artifactManager.saveApiLogs(executionId, testCaseName, logContent);

            // --- FIX: Use result.getName() ---
            result.getTestContext().setAttribute("artifactPath_" + testCaseName, artifactPath);
        }
    }
}