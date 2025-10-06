package com.testframework.core;

import com.testframework.util.ArtifactManager;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import java.time.Duration;

@SpringBootTest
public abstract class BaseUiTest extends AbstractTestNGSpringContextTests {

    @Autowired
    protected ArtifactManager artifactManager;

    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    public WebDriver getDriver() {
        return driver.get();
    }

    @BeforeMethod
    @Parameters({"pageLoadTimeout", "elementWaitTimeout"})
    public void setUp(String pageLoadTimeout, String elementWaitTimeout) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--no-sandbox"); // <-- ADD THIS
        options.addArguments("--disable-dev-shm-usage"); // <-- ADD THIS
        options.addArguments("--window-size=1920,1080"); // <-- ADD THIS
        driver.set(new ChromeDriver(options));

        getDriver().manage().window().maximize();
        getDriver().manage().timeouts().pageLoadTimeout(Duration.ofSeconds(Long.parseLong(pageLoadTimeout)));
        getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(Long.parseLong(elementWaitTimeout)));
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        if (result.getThrowable() != null) {
            // Get executionId from the suite attribute we set in the listener.
            long executionId = (Long) result.getTestContext().getSuite().getAttribute("executionId");
            String testCaseName = result.getName();

            // The ArtifactManager now handles saving the screenshot.
            artifactManager.captureScreenshot(getDriver(), executionId, testCaseName);
        }

        if (getDriver() != null) {
            getDriver().quit();
        }
        driver.remove();
    }
}