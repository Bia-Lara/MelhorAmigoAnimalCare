package br.ifsp.demo.ui.tests;

import br.ifsp.demo.ui.base.BaseSeleniumTest;
import br.ifsp.demo.ui.pages.LoginPageObject;
import br.ifsp.demo.ui.pages.RegisterPageObject;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("UITest")
public class RegisterUiTest extends BaseSeleniumTest {
    private RegisterPageObject registerPage;
    private final Faker faker = new Faker();

    @Override
    protected void setInitialPage(){
        registerPage = new RegisterPageObject(driver);
        registerPage.openRegisterPage();
    }

    @Test
    @DisplayName("shouldRegisterAccountAndRedirectToLoginWithSuccessMessage")
    void shouldRegisterAccountAndRedirectToLoginWithSuccessMessage() {
        String email = faker.internet().emailAddress();
        String password = faker.internet().password(6, 10);

        registerPage.registerAccount(faker.name().firstName(), faker.name().lastName(), email, password, password);
        assertThat(registerPage.currentUrl()).endsWith("/login");
        assertThat(new LoginPageObject(driver).successMessage())
                .isEqualTo("Conta criada! Entre com " + email + ".");
    }

    @Test
    @DisplayName("shouldShowRequiredMessagesWhenSubmittingEmptyForm")
    void shouldShowRequiredMessagesWhenSubmittingEmptyForm() {
        registerPage.clickRegister();

        assertThat(registerPage.fieldErrorMessages()).containsExactly(
                "Informe o nome.",
                "Informe o sobrenome.",
                "Informe o e-mail.",
                "Informe a senha.");
        assertThat(registerPage.currentUrl()).endsWith("/registrar");
    }

    @Test
    @DisplayName("shouldRejectMismatchedPasswordConfirmation")
    void shouldRejectMismatchedPasswordConfirmation() {
        registerPage.submitRegistrationForm(
                faker.name().firstName(),
                faker.name().lastName(),
                faker.internet().emailAddress(),
                "senha123", "senha456");

        assertThat(registerPage.fieldErrorMessages())
                .containsExactly("As senhas não conferem.");
        assertThat(registerPage.currentUrl()).endsWith("/registrar");
    }

    @Test
    @DisplayName("shouldRejectInvalidEmailFormat")
    void shouldRejectInvalidEmailFormat() {
        registerPage.submitRegistrationForm(
                faker.name().firstName(),
                faker.name().lastName(),
                "email-invalido",
                "senha123", "senha123");

        assertThat(registerPage.fieldErrorMessages())
                .containsExactly("E-mail inválido.");
        assertThat(registerPage.currentUrl()).endsWith("/registrar");
    }

    @Test
    @DisplayName("shouldRejectDuplicateEmail")
    void shouldRejectDuplicateEmail() {
        String email = faker.internet().emailAddress();
        String password = faker.internet().password(6, 10);

        registerPage.registerAccount(faker.name().firstName(), faker.name().lastName(), email, password, password);
        registerPage.openRegisterPage();

        registerPage.submitRegistrationForm(faker.name().firstName(), faker.name().lastName(), email, password, password);

        assertThat(registerPage.registerErrorMessage()).isEqualTo("Email already registered: " + email);
        assertThat(registerPage.currentUrl()).endsWith("/registrar");
    }

    @ParameterizedTest(name = "{0} ({1}x{2})")
    @CsvSource({
            "Mobile pequeno, 320, 568",
            "Mobile, 375, 667",
            "Mobile grande, 414, 896",
            "Tablet retrato, 768, 1024",
            "Tablet paisagem, 1024, 768",
            "Laptop, 1366, 768",
            "Desktop Full HD, 1920, 1080"
    })
    @DisplayName("shouldDisplayRegisterFormWithoutHorizontalScroll")
    void shouldDisplayRegisterFormWithoutHorizontalScroll(String device, int width, int height) {
        registerPage.emulateViewport(width, height);

        assertThat(registerPage.isFormFullyVisible()).isTrue();
        assertThat(registerPage.hasHorizontalScroll())
                .as("scroll horizontal em " + device)
                .isFalse();
    }
}
