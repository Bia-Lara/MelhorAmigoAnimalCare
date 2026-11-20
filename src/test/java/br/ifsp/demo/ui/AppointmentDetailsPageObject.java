package br.ifsp.demo.ui;

import com.github.javafaker.App;
import com.github.javafaker.Weather;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class AppointmentDetailsPageObject extends BasePageObject{
    private final By appointmentId = By.cssSelector(".header-card h1.h-id");

    public AppointmentDetailsPageObject(WebDriver driver){
        super(driver);
    }

    public String appointmentId(){
        return new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(appointmentId))
                .getText();
    }

}


