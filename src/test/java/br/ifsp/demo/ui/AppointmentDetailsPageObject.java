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
    private final By loadError = By.cssSelector(".alert--error > span");
    private final By cancelButton = By.xpath(  "//section[contains(@class, 'header-card')]//button[normalize-space()='Cancelar atendimento']");
    private final By cancellationReasonInput = By.cssSelector("dialog[open] textarea");
    private final By confirmCancelButton = By.cssSelector("dialog[open] button[type='submit']");
    private final By cancelledStatus = By.cssSelector(".header-card .badge--cancelado");
    private final By temperatureInput = By.name("temperaturaC");
    private final By heartRateInput = By.name("frequenciaCardiaca");
    private final By weightInput = By.name("pesoKg");
    private final By registerVitalsButton =
            By.cssSelector(".grid--2 .card:first-child form button[type='submit']");
    private final By vitalsSavedMessage =
            By.cssSelector(".grid--2 .card:first-child .alert--success");

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

    public String loadErrorMessage(){
        return new WebDriverWait(driver,Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(loadError))
                .getText();
    }

    public void cancelAppointment(String justification){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        wait.until(ExpectedConditions.elementToBeClickable(cancelButton)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(cancellationReasonInput)).sendKeys(justification);
        wait.until(ExpectedConditions.elementToBeClickable(confirmCancelButton)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(cancelledStatus));
    }

    public void registerVitalSigns(String temperature, String heartRate, String weight){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        wait.until(ExpectedConditions.visibilityOfElementLocated(temperatureInput)).sendKeys(temperature);
        wait.until(ExpectedConditions.visibilityOfElementLocated(heartRateInput)).sendKeys(heartRate);
        wait.until(ExpectedConditions.visibilityOfElementLocated(weightInput)).sendKeys(weight);
        wait.until(ExpectedConditions.elementToBeClickable(registerVitalsButton)).click();

    }

    public String vitalsSaveMessage(){
        return new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(vitalsSavedMessage)).getText();
    }
}


