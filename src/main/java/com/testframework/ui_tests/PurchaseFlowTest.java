package com.testframework.ui_tests;

import com.testframework.core.BaseUiTest;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PurchaseFlowTest extends BaseUiTest {

    @Test(testName = "TC-UI-08")
    public void completeFlightPurchase() {
        getDriver().get("https://blazedemo.com/purchase.php");
        getDriver().findElement(By.id("inputName")).sendKeys("John Doe");
        getDriver().findElement(By.id("address")).sendKeys("123 Main St");
        getDriver().findElement(By.id("city")).sendKeys("Anytown");
        getDriver().findElement(By.cssSelector("input.btn.btn-primary")).click();
        String confirmationText = getDriver().findElement(By.tagName("h1")).getText();
        Assert.assertEquals(confirmationText, "Thank you for your purchase today!", "Purchase confirmation failed.");
    }

    @Test(testName = "TC-UI-09")
    public void verifyConfirmationDetails() {
        getDriver().get("https://blazedemo.com/confirmation.php");
        String title = getDriver().getTitle();
        Assert.assertEquals(title, "BlazeDemo Confirmation", "Not on the confirmation page.");
    }

    @Test(testName = "TC-UI-10")
    public void testPurchaseWithEmptyName() {
        getDriver().get("https://blazedemo.com/purchase.php");
        getDriver().findElement(By.id("address")).sendKeys("123 Main St");
        getDriver().findElement(By.cssSelector("input.btn.btn-primary")).click();
        Assert.assertEquals(getDriver().getTitle(), "BlazeDemo Confirmation", "Form submission failed with empty name.");
    }

    @Test(testName = "TC-UI-11")
    public void verifyCardYear() {
        getDriver().get("https://blazedemo.com/purchase.php");
        String currentYear = java.time.LocalDate.now().getYear() + "";
        String lastYearInDropdown = getDriver().findElement(By.id("year")).getAttribute("value");
        Assert.assertEquals(lastYearInDropdown, currentYear, "Credit card year is not current.");
    }
}