🚀 API-UI Automation Scheduler Framework
📌 Project Description

This project is an end-to-end regression testing framework for automating API and UI test cases with scheduling, parallel execution, reporting, and result analytics.
It integrates Selenium for web UI testing, REST-Assured for API validation, and Spring Boot for REST APIs to manage test execution, scheduling, and reporting.

🔹 Features

✅ UI Automation with Selenium (BlazeDemo sample site)

✅ API Automation with REST-Assured (ReqRes sample API)

✅ Parallel Execution using TestNG suites (UI = 4 threads, API = 8 threads)

✅ Scheduler to trigger test runs automatically (@Scheduled, cron jobs, or Jenkins)

✅ Reporting in HTML, CSV, JUnit formats

✅ Artifacts Storage – Screenshots, API logs, failure details

✅ Database Integration (MySQL/PostgreSQL) for storing test executions and results

✅ Spring Boot REST APIs for managing test runs and fetching results

✅ Result Analytics for tracking trends, pass/fail rates, execution time

🔹 Tech Stack

Language: Java (JDK 17+)

Frameworks: Spring Boot, TestNG

Libraries: Selenium, REST-Assured, ExtentReports / Allure

Database: MySQL / PostgreSQL (with Flyway migrations)

Build Tool: Maven

Version Control: Git & GitHub

🔹 Project Modules

Test Integration Engine – Handles UI & API test creation & execution.

Scheduler & Execution System – Runs test suites (manual or scheduled).

Reporting & Log Hub – Generates reports and stores artifacts.

Result Analytics Tracker – Provides execution statistics and trends.

🔹 REST API Endpoints
Method	Endpoint	Description
POST	/schedule/run	Trigger test suite run (UI/API)
GET	/executions/{id}/status	Check execution status
GET	/reports/{executionId}/download	Download HTML/CSV/JUnit report
GET	/artifacts/{artifactId}	Download screenshots / API logs
GET	/analytics/trends	View historical execution trends
🔹 Sample Test Cases
🔸 UI (BlazeDemo)

Navigate to BlazeDemo

Select departure & destination city

Verify available flights

Book a flight & confirm purchase

🔸 API (ReqRes)

GET users → Verify status 200 & response

POST user → Verify status 201 & response body

PUT user → Verify update success

DELETE user → Verify 204 No Content

🔹 Folder Structure
api-ui-automation-scheduler/
 ├── src/main/java/com/testframework/
 │   ├── controller/   # REST Controllers
 │   ├── service/      # Business Logic
 │   ├── repository/   # DB Repositories
 │   ├── model/        # Entities (TestCase, Execution, Result)
 │   ├── scheduler/    # Scheduled Jobs
 │   └── tests/        # UI + API Tests
 ├── src/test/resources/
 │   ├── blaze_smoke.xml   # TestNG Suite for UI
 │   └── reqres_smoke.xml  # TestNG Suite for API
 ├── reports/              # HTML, CSV, JUnit reports
 ├── artifacts/            # Screenshots, API logs
 ├── pom.xml               # Maven dependencies
 └── README.md             # Project Documentation

🔹 Setup Instructions
1️⃣ Clone Repo
git clone https://github.com/<your-username>/api-ui-automation-scheduler.git
cd api-ui-automation-scheduler

2️⃣ Configure Database (MySQL Example)
spring.datasource.url=jdbc:mysql://localhost:3306/testdb
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

3️⃣ Run Database Migration
mvn flyway:migrate

4️⃣ Run Spring Boot App
mvn spring-boot:run

5️⃣ Execute TestNG Suites
mvn clean test -DsuiteXmlFile=src/test/resources/blaze_smoke.xml
mvn clean test -DsuiteXmlFile=src/test/resources/reqres_smoke.xml

🔹 Reports & Artifacts

Reports: /reports/{executionId}/report.html

Artifacts: /artifacts/{executionId}/{testCaseId}/


🔹 Outcome

Faster regression runs with parallel execution

Automated test scheduling with Spring Boot APIs

Centralized reports & artifacts for debugging

Reliable analytics dashboard for QA & Dev teams
