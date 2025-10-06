package com.testframework.service;

import com.testframework.model.TestCase;
import com.testframework.repository.TestCaseRepository;
import com.testframework.repository.TestSuiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TestCaseService {

    private final TestCaseRepository testCaseRepository;
    private final TestSuiteRepository testSuiteRepository;

    @Autowired
    public TestCaseService(TestCaseRepository testCaseRepository, TestSuiteRepository testSuiteRepository) {
        this.testCaseRepository = testCaseRepository;
        this.testSuiteRepository = testSuiteRepository;
    }

    public TestCase createTestCase(TestCase testCase) {
        // Ensure the associated suite exists
        testSuiteRepository.findById(testCase.getTestSuite().getId())
                .orElseThrow(() -> new IllegalArgumentException("TestSuite with ID " + testCase.getTestSuite().getId() + " not found."));
        return testCaseRepository.save(testCase);
    }

    public Page<TestCase> getAllTestCases(Pageable pageable) {
        return testCaseRepository.findAll(pageable);
    }

    public Optional<TestCase> getTestCaseById(Long id) {
        return testCaseRepository.findById(id);
    }

    public Optional<TestCase> updateTestCase(Long id, TestCase testCaseDetails) {
        return testCaseRepository.findById(id).map(existingCase -> {
            existingCase.setName(testCaseDetails.getName());
            existingCase.setDescription(testCaseDetails.getDescription());
            existingCase.setPriority(testCaseDetails.getPriority());
            existingCase.setTestType(testCaseDetails.getTestType());
            existingCase.setTestSuite(testCaseDetails.getTestSuite());
            return testCaseRepository.save(existingCase);
        });
    }

    public boolean deleteTestCase(Long id) {
        if (testCaseRepository.existsById(id)) {
            testCaseRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<TestCase> getTestCasesBySuite(Long suiteId) {
        return testCaseRepository.findByTestSuiteId(suiteId);
    }
}