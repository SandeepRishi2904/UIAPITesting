package com.testframework.ui_tests;

import com.testframework.core.BaseUiTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.List;

public class FlightResultTest extends BaseUiTest {

    @Test(testName = "TC-UI-05")
    public void verifyResultsTableHeaders() {
        getDriver().get("https://blazedemo.com/reserve.php?fromPort=Paris&toPort=Rome");
        List<WebElement> headers = getDriver().findElements(By.xpath("//table/thead/tr/th"));
        Assert.assertEquals(headers.get(0).getText(), "Choose", "Header 'Choose' not found.");
        Assert.assertEquals(headers.get(3).getText(), "Departs: Paris", "Header 'Departs' not found.");
    }

    @Test(testName = "TC-UI-06")
    public void chooseSpecificFlight() {
        getDriver().get("https://blazedemo.com/reserve.php?fromPort=Boston&toPort=London");
        getDriver().findElement(By.xpath("//tr[td[2]='43']/td[1]/input")).click();
        String headerText = getDriver().findElement(By.tagName("h2")).getText();
        Assert.assertTrue(headerText.contains("Your flight from Boston to London has been reserved."), "Did not land on purchase page for the correct flight.");
    }

    @Test(testName = "TC-UI-07")
    public void failingTestForScreenshot() {
        getDriver().get("https://blazedemo.com/");
        Assert.fail("This test is designed to fail to demonstrate screenshot capture.");
    }
}