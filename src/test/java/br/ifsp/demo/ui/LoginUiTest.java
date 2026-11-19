package br.ifsp.demo.ui;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@Tag("UiTest")
public class LoginUiTest extends BaseSeleniumTest{
    private LoginPageObject loginPage;

    @Override
    protected void setInitialPage(){
        loginPage = new LoginPageObject(driver);
        loginPage.openLoginPage();
    }

    @Test
    @DisplayName("shouldShowRequiredMessagesWhenSubmittingEmptyForm")
    void shouldShowRequiredMessagesWhenSubmittingEmptyForm() {
        loginPage.clickLogin();
        assertThat(loginPage.fieldErrorMessages()).contains("Informe o e-mail.", "Informe a senha.");

    }
}
