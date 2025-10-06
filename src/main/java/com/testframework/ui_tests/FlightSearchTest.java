package com.testframework.ui_tests;

import com.testframework.core.BaseUiTest;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FlightSearchTest extends BaseUiTest {

    @Test(testName = "TC-UI-01")
    public void verifyPageTitle() {
        getDriver().get("https://blazedemo.com/");
        Assert.assertEquals(getDriver().getTitle(), "BlazeDemo", "Page title is incorrect.");
    }

    @Test(testName = "TC-UI-02")
    public void findFlightsParisToRome() {
        getDriver().get("https://blazedemo.com/");
        getDriver().findElement(By.name("fromPort")).sendKeys("Paris");
        getDriver().findElement(By.name("toPort")).sendKeys("Rome");
        getDriver().findElement(By.cssSelector("input.btn.btn-primary")).click();
        String headerText = getDriver().findElement(By.tagName("h3")).getText();
        Assert.assertTrue(headerText.contains("Flights from Paris to Rome"), "Did not land on the results page for Paris to Rome.");
    }

    @Test(testName = "TC-UI-03")
    public void findFlightsBostonToLondon() {
        getDriver().get("https://blazedemo.com/");
        getDriver().findElement(By.name("fromPort")).sendKeys("Boston");
        getDriver().findElement(By.name("toPort")).sendKeys("London");
        getDriver().findElement(By.cssSelector("input.btn.btn-primary")).click();
        String headerText = getDriver().findElement(By.tagName("h3")).getText();
        Assert.assertTrue(headerText.contains("Flights from Boston to London"), "Did not land on the results page for Boston to London.");
    }

    @Test(testName = "TC-UI-04")
    public void verifyDestinationOfTheWeekLink() {
        getDriver().get("https://blazedemo.com/");
        getDriver().findElement(By.linkText("destination of the week! The Beach!")).click();
        Assert.assertTrue(getDriver().getCurrentUrl().contains("vacation.html"), "Destination of the week link is broken.");
    }
}