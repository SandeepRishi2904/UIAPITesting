package com.testframework.repository;

import com.testframework.model.TestExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TestExecutionRepository extends JpaRepository<TestExecution, Long> {

    Set<TestExecution> findByTestSuiteId(Long testSuiteId);

    Optional<TestExecution> findTopByTestSuiteIdOrderByStartedAtDesc(Long testSuiteId);

    @Query("SELECT te FROM TestExecution te " +
            "LEFT JOIN FETCH te.testSuite ts " +
            "LEFT JOIN FETCH ts.testCases " +
            "LEFT JOIN FETCH te.testResults tr " +
            "WHERE te.id = :executionId")
    Optional<TestExecution> findByIdWithAllDetails(@Param("executionId") Long executionId);

    // --- THIS IS THE CORRECTED METHOD ---
    // The @Query annotation is required because the method name is too complex for Spring to parse automatically.
    @Query("SELECT te FROM TestExecution te WHERE te.testSuite.id = :suiteId AND te.startedAt BETWEEN :startDate AND :endDate")
    List<TestExecution> findByTestSuiteIdAndStartedAtBetween(@Param("suiteId") Long suiteId,
                                                             @Param("startDate") LocalDateTime startDate,
                                                             @Param("endDate") LocalDateTime endDate);
}