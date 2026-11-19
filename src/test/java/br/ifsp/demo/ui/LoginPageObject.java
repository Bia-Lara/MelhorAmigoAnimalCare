package br.ifsp.demo.ui;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class LoginPageObject extends BaseSeleniumTest{
    private final By emailInput = By.name("email");
    private final By passwordInput = By.name("password");
    private final By loginButton = By.cssSelector("form button[type='submit']");
    private final By fieldErrors = By.cssSelector(".field__error");
    private final By loginError = By.cssSelector(".alert--error");
    private final By registerLink = By.linkText("Cadastre-se");

    public LoginPageObject(WebDriver driver){
        super(driver);
    }

    public void openLoginPage(){
        driver.get("https://localhost:5173/login");
    }

    public void authenticate(String email, String password){
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(emailInput))
                .sendKeys(email);

        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(passwordInput))
                .sendKeys(email);

        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(loginButton))
                .click();

    }

    public void clickLogin(){
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(loginButton))
                .click();
    }

    public List<String> fieldErrorMessages(){
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(fieldErrors));

        return driver.findElements(fieldErrors)
                .stream()
                .map(WebElement::getText)
                .toList();
    }

    public String loginErrorMessage(){
        return new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(loginError))
                .getText();
    }
}
