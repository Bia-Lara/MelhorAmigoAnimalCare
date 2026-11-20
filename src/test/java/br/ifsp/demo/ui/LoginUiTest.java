package br.ifsp.demo.ui;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@Tag("UiTest")
public class LoginUiTest extends BaseSeleniumTest{
    private LoginPageObject loginPage;
    private static final String TEST_EMAIL = "teste@email.com";
    private static final String TEST_PASSWORD = "123456";

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

    @Test
    @DisplayName("shouldMaskPassword")
    void shouldMaskPassword() {
        assertThat(loginPage.passwordFieldType()).isEqualTo("password");
    }

    @Test
    @DisplayName("shouldRequireEmail")
    void shouldRequireEmail() {
        loginPage.enterPassword("teste");
        loginPage.clickLogin();

        assertThat(loginPage.fieldErrorMessages()).containsExactly("Informe o e-mail.");

    }

    @Test
    @DisplayName("shouldRequirePassword")
    void shouldRequirePassword() {
        loginPage.enterEmail("teste");
        loginPage.clickLogin();

        assertThat(loginPage.fieldErrorMessages()).containsExactly("Informe a senha.");

    }

    @Test
    @DisplayName("shouldRejectWhitespaceOnlyValues")
    void shouldRejectWhitespaceOnlyValues() {
        loginPage.authenticate(" ", " ");

        assertThat(loginPage.fieldErrorMessages())
                .contains("Informe o e-mail.", "Informe a senha.");
    }

    @Test
    @DisplayName("shouldOpenAppointmentPageValidCredentials")
    void shouldOpenAppointmentPageValidCredentials() {
        loginPage.authenticate(TEST_EMAIL, TEST_PASSWORD);

        AppointmentPageObject appointmentPage = new AppointmentPageObject(driver);

        assertThat(appointmentPage.heading()).isEqualTo("Atendimentos");


    }

}
