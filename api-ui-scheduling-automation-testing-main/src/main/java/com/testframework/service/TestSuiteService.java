package com.testframework.service;

import com.testframework.model.TestSuite;
import com.testframework.repository.TestSuiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TestSuiteService {

    private final TestSuiteRepository testSuiteRepository;

    @Autowired
    public TestSuiteService(TestSuiteRepository testSuiteRepository) {
        this.testSuiteRepository = testSuiteRepository;
    }

    public TestSuite createTestSuite(TestSuite testSuite) {
        if (testSuiteRepository.existsByNameIgnoreCase(testSuite.getName())) {
            throw new IllegalArgumentException("A test suite with the name '" + testSuite.getName() + "' already exists.");
        }
        return testSuiteRepository.save(testSuite);
    }

    public Page<TestSuite> getAllTestSuites(Pageable pageable) {
        return testSuiteRepository.findAll(pageable);
    }

    public Optional<TestSuite> getTestSuiteById(Long id) {
        return testSuiteRepository.findById(id);
    }

    public Optional<TestSuite> updateTestSuite(Long id, TestSuite testSuiteDetails) {
        return testSuiteRepository.findById(id).map(existingSuite -> {
            existingSuite.setName(testSuiteDetails.getName());
            existingSuite.setDescription(testSuiteDetails.getDescription());
            existingSuite.setTestType(testSuiteDetails.getTestType());
            existingSuite.setSuiteXmlFile(testSuiteDetails.getSuiteXmlFile());
            return testSuiteRepository.save(existingSuite);
        });
    }

    public boolean deleteTestSuite(Long id) {
        if (testSuiteRepository.existsById(id)) {
            testSuiteRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<TestSuite> getTestSuiteWithCases(Long id) {
        return testSuiteRepository.findByIdWithTestCases(id);
    }
}