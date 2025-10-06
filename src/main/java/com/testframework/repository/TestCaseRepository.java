package com.testframework.repository;

import com.testframework.model.Priority;
import com.testframework.model.TestCase;
import com.testframework.model.TestType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional; // <-- ADD THIS IMPORT

@Repository
public interface TestCaseRepository extends JpaRepository<TestCase, Long> {

    // --- ADD THIS METHOD ---
    /**
     * Find a TestCase by its unique name (which we use as the Test Case ID).
     */
    Optional<TestCase> findByName(String name);

    List<TestCase> findByTestSuiteId(Long testSuiteId);

    List<TestCase> findByTestType(TestType testType);

    List<TestCase> findByPriority(Priority priority);

    List<TestCase> findByTestSuiteIdAndPriority(Long testSuiteId, Priority priority);

    List<TestCase> findByNameContainingIgnoreCase(String name);

    Long countByTestSuiteId(Long testSuiteId);

    @Query("SELECT DISTINCT tc FROM TestCase tc " +
            "WHERE tc.testSuite.id = :testSuiteId " +
            "ORDER BY tc.priority DESC, tc.name ASC")
    List<TestCase> findByTestSuiteIdOrderByPriority(@Param("testSuiteId") Long testSuiteId);

    List<TestCase> findByTestSuiteIdAndTestType(Long testSuiteId, TestType testType);
}