package com.testframework.service;

import com.testframework.config.TestExecutionListener;
import com.testframework.model.ExecutionStatus;
import com.testframework.model.TestExecution;
import com.testframework.repository.TestExecutionRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.testng.TestNG;
import org.testng.xml.Parser;
import org.testng.xml.XmlSuite;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TestRunnerService {

    private static final Logger logger = LoggerFactory.getLogger(TestRunnerService.class);

    @Autowired
    private TestExecutionRepository testExecutionRepository;

    @Autowired
    private ApplicationContext context;

    @Value("${ui.page.load.timeout.seconds:60}")
    private String pageLoadTimeout;

    @Value("${ui.element.wait.timeout.seconds:20}")
    private String elementWaitTimeout;

    @Async
    @Transactional
    public void runTestSuite(Long executionId) {
        TestExecution execution = testExecutionRepository.findById(executionId)
                .orElseThrow(() -> new IllegalStateException("Execution not found for ID: " + executionId));

        try {
            logger.info("Starting test execution ID: {}", executionId);
            execution.setStatus(ExecutionStatus.RUNNING);
            testExecutionRepository.save(execution);

            TestNG testng = new TestNG();
            TestExecutionListener listener = context.getBean(TestExecutionListener.class);
            listener.setExecutionId(executionId);
            testng.addListener(listener);

            String suiteResourcePath = "suites/" + execution.getTestSuite().getSuiteXmlFile();
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(suiteResourcePath);
            if (inputStream == null) {
                throw new IllegalArgumentException("Cannot find test suite file on classpath: " + suiteResourcePath);
            }
            List<XmlSuite> suites = new Parser(inputStream).parseToList();

            XmlSuite suite = suites.get(0);

            // The incorrect suite.setAttribute() line has been removed.

            Map<String, String> parameters = new HashMap<>();
            parameters.put("pageLoadTimeout", pageLoadTimeout);
            parameters.put("elementWaitTimeout", elementWaitTimeout);
            suite.setParameters(parameters);

            testng.setXmlSuites(suites);
            testng.setParallel(suite.getParallel());
            testng.setThreadCount(suite.getThreadCount());

            logger.info("TestNG is now executing the '{}' suite file.", execution.getTestSuite().getSuiteXmlFile());

            testng.run();

            logger.info("Finished test execution ID: {}", executionId);

        } catch (Exception e) {
            logger.error("Error during test execution ID: {}", executionId, e);
            TestExecution finalExecution = testExecutionRepository.findById(executionId).orElseThrow();
            finalExecution.setStatus(ExecutionStatus.FAILED);
            finalExecution.setEndedAt(LocalDateTime.now());
            testExecutionRepository.save(finalExecution);
        }
    }
}