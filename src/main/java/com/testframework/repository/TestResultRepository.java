package com.testframework.repository;

import com.testframework.model.TestResult;
import com.testframework.model.TestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for TestResult entity operations.
 */
@Repository
public interface TestResultRepository extends JpaRepository<TestResult, Long> {

    /**
     * Find results by test execution ID.
     */
    List<TestResult> findByTestExecutionId(Long testExecutionId);

    /**
     * Find results by test case ID.
     */
    List<TestResult> findByTestCaseId(Long testCaseId);

    /**
     * Find results by status.
     */
    List<TestResult> findByStatus(TestStatus status);

    /**
     * Find results by test execution ID and status.
     */
    List<TestResult> findByTestExecutionIdAndStatus(Long testExecutionId, TestStatus status);

    /**
     * Find results executed after a specific date.
     */
    List<TestResult> findByExecutedAtAfter(LocalDateTime executedAt);

    /**
     * Get latest result for a test case.
     */
    Optional<TestResult> findTopByTestCaseIdOrderByExecutedAtDesc(Long testCaseId);

    /**
     * Get test result statistics for an execution.
     */
    @Query("SELECT tr.status, COUNT(tr) FROM TestResult tr " +
            "WHERE tr.testExecution.id = :executionId " +
            "GROUP BY tr.status")
    List<Object[]> getResultStatistics(@Param("executionId") Long executionId);

    /**
     * Find failed results with artifacts.
     */
    // --- THIS IS THE FIX ---
    // The query now correctly uses 'artifactPath' instead of 'screenshotPath'.
    @Query("SELECT tr FROM TestResult tr " +
            "WHERE tr.status = com.testframework.model.TestStatus.FAILED AND tr.artifactPath IS NOT NULL")
    List<TestResult> findFailedResultsWithScreenshots();

    /**
     * Get average execution time for a test case.
     */
    @Query("SELECT AVG(tr.executionTimeMs) FROM TestResult tr " +
            "WHERE tr.testCase.id = :testCaseId AND tr.status = com.testframework.model.TestStatus.PASSED")
    Double getAverageExecutionTimeByTestCaseId(@Param("testCaseId") Long testCaseId);

    /**
     * Find results by test case over time for trend analysis.
     */
    @Query("SELECT tr FROM TestResult tr " +
            "WHERE tr.testCase.id = :testCaseId " +
            "AND tr.executedAt BETWEEN :startDate AND :endDate " +
            "ORDER BY tr.executedAt ASC")
    List<TestResult> findByTestCaseIdBetweenDates(@Param("testCaseId") Long testCaseId,
                                                  @Param("startDate") LocalDateTime startDate,
                                                  @Param("endDate") LocalDateTime endDate);
}