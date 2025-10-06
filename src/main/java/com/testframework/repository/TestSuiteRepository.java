package com.testframework.repository;

import com.testframework.model.TestSuite;
import com.testframework.model.TestType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for TestSuite entity operations.
 */
@Repository
public interface TestSuiteRepository extends JpaRepository<TestSuite, Long> {

    /**
     * Find test suite by name.
     */
    Optional<TestSuite> findByName(String name);

    /**
     * Find test suites by type.
     */
    List<TestSuite> findByTestType(TestType testType);

    /**
     * Find test suites created after a specific date.
     */
    List<TestSuite> findByCreatedAtAfter(LocalDateTime createdAt);

    /**
     * Find test suites with test cases count.
     */
    @Query("SELECT ts FROM TestSuite ts LEFT JOIN FETCH ts.testCases WHERE ts.id = :id")
    Optional<TestSuite> findByIdWithTestCases(@Param("id") Long id);

    /**
     * Get test suites with their execution count.
     */
    @Query("SELECT ts, COUNT(te) as executionCount FROM TestSuite ts " +
            "LEFT JOIN ts.executions te " +
            "GROUP BY ts.id")
    List<Object[]> findAllWithExecutionCount();

    /**
     * Check if test suite exists by name (case insensitive).
     */
    boolean existsByNameIgnoreCase(String name);
}