package br.ifsp.demo.ui;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class RegisterPageObject extends BasePageObject{
    private final By nameInput= By.name("name");
    private final By lastNameInput = By.name("lastname");
    private final By emailInput = By.name("email");
    private final By passwordInput = By.name("password");
    private final By confirmPasswordInput = By.name("confirmPassword");
    private final By registerButton = By.cssSelector("form button[type='submit']");

    public RegisterPageObject(WebDriver driver){
        super(driver);
    }

    public void openRegisterPage(){
        driver.get("http://localhost:5173/registrar");
    }

}
