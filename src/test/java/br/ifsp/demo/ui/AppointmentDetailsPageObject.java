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
    private final By appointmentOrigin =
            By.xpath("//dt[normalize-space()='Origem']/following-sibling::dd");
    private final By backToAppointmentsLink = By.cssSelector("a.back-link");
    private final By animalId = By.xpath("//dt[normalize-space()='Animal']/following-sibling::dd");

    public AppointmentDetailsPageObject(WebDriver driver){
        super(driver);
    }

    public String appointmentId(){
        return new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(appointmentId))
                .getText();
    }

    public String appointmentOrigin(){
        return new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(appointmentOrigin))
                .getText();
    }

    public AppointmentPageObject backToAppointments(){
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(backToAppointmentsLink))
                .click();

        AppointmentPageObject page = new AppointmentPageObject(driver);
        page.heading();
        return page;
    }

    public String animalId(){
        return new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(animalId))
                .getText();
    }

}


