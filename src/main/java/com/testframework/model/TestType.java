package com.testframework.model;

/**
 * Enumeration for different types of tests supported by the framework.
 */
public enum TestType {
    WEB_UI("Web UI Testing using Selenium"),
    API("API Testing using REST-Assured"),
    MIXED("Combined Web UI and API Testing");

    private final String description;

    TestType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}