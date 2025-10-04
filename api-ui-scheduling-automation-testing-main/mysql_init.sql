-- MySQL initialization script for TestPilot
CREATE DATABASE IF NOT EXISTS testpilot CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE testpilot;

CREATE TABLE IF NOT EXISTS executions (
  execution_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  suite_name VARCHAR(255),
  start_time DATETIME,
  end_time DATETIME,
  status VARCHAR(50),
  total_tests INT,
  passed_tests INT,
  failed_tests INT,
  thread_count INT,
  created_by VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS test_results (
  result_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  execution_id BIGINT,
  testcase_id VARCHAR(100),
  testcase_name VARCHAR(255),
  suite_name VARCHAR(255),
  status VARCHAR(50),
  start_time DATETIME,
  end_time DATETIME,
  duration_seconds INT,
  error_message TEXT,
  artifact_path VARCHAR(1024),
  FOREIGN KEY (execution_id) REFERENCES executions(execution_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS artifacts (
  artifact_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  execution_id BIGINT,
  result_id BIGINT,
  artifact_type VARCHAR(50),
  path VARCHAR(1024),
  created_at DATETIME,
  FOREIGN KEY (execution_id) REFERENCES executions(execution_id) ON DELETE CASCADE,
  FOREIGN KEY (result_id) REFERENCES test_results(result_id) ON DELETE CASCADE
);

-- Indexes
CREATE INDEX idx_exec_status ON executions(status);
CREATE INDEX idx_result_status ON test_results(status);
