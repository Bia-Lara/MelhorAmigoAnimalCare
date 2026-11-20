package br.ifsp.demo.ui;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class AppointmentPageObject extends BasePageObject{
    private final By heading = By.cssSelector(".page-head h1");
    private final By openingTypeButtons = By.cssSelector(".segmented button[role='radio'");
    private final By animalSelect = By.cssSelector(".card select");
    private final By openAppointmentButton = By.cssSelector(".grid--2 .card:first-child form button[type='submit'");
    private final By openingError = By.cssSelector(".alert--error");
    private final By appointmentIdInput = By.cssSelector("input[placeholder='UUID']");
    private final By searchButton = By.cssSelector(".form--inline button[type='submit']");
    private final By recentAppointments = By.cssSelector(".list__row");
    private final By recentEmptyMessage = By.cssSelector(".card .empty");
    private final By clearRecentButton = By.cssSelector(".card__actions button");

    public AppointmentPageObject(WebDriver driver){
        super(driver);
    }

    public String heading(){
        return new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(heading))
                .getText();
    }

    public void selectAppointment(){
        selectOpeningType("Pronto atendimnto");
    }

    public void selectScheduledAppointment(){
        selectOpeningType("Com agendamento");
    }

    private void selectOpeningType(String label) {
        for (WebElement button : driver.findElements(openingTypeButtons)) {
            if (button.getText().equals(label)) {
                button.click();
                return;
            }
        }
        throw new NoSuchElementException("Opening type not found: " + label);
    }


}
