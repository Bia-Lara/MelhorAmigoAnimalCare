package br.ifsp.demo.ui;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class AppointmentPageObject extends BasePageObject{
    private final By heading = By.cssSelector(".page-head h1");

    public AppointmentPageObject(WebDriver driver){
        super(driver);
    }

    public String heading(){
        return new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(heading))
                .getText();
    }
}
