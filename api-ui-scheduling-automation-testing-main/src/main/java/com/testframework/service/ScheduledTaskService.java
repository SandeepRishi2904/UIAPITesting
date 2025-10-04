package com.testframework.service;

import com.testframework.controller.TestExecutionController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduledTaskService {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTaskService.class);

    @Autowired
    private TestExecutionController testExecutionController;

    /**
     * Schedules the UI test suite (ID 1) to run automatically.
     * The cron expression "0 0 2 * * ?" means it will run every day at 2:00 AM.
     */
    @Scheduled(cron = "0 27 23 * * ?")
    public void scheduleUiTestSuite() {
        log.info("Executing scheduled run for UI Test Suite (ID 1)...");
        testExecutionController.scheduleTestRun(1L);
    }

    /**
     * Schedules the API test suite (ID 2) to run automatically.
     * The cron expression "0 0/15 * * * ?" means it will run every 15 minutes.
     */
    @Scheduled(cron = "0 07 23 * * ?")
    public void scheduleApiTestSuite() {
        log.info("Executing scheduled run for API Test Suite (ID 2)...");
        testExecutionController.scheduleTestRun(2L);
    }

    /**
     * --- THIS IS THE NEW METHOD ---
     * Schedules the Full Regression suite (ID 3) to run automatically.
     * The cron expression "0 0 3 * * ?" means it will run every day at 3:00 AM.
     */
    @Scheduled(cron = "0 08 23 * * ?")
    public void scheduleFullRegressionSuite() {
        log.info("Executing scheduled run for Full Regression Suite (ID 3)...");
        testExecutionController.scheduleTestRun(3L);
    }
}