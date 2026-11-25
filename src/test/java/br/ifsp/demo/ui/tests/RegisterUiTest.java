package br.ifsp.demo.ui.tests;

import br.ifsp.demo.ui.base.BaseSeleniumTest;
import br.ifsp.demo.ui.pages.LoginPageObject;
import br.ifsp.demo.ui.pages.RegisterPageObject;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

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

    @Test
    @DisplayName("Should reject without name")
    void shouldRejectWithoutName() {
        String email = faker.internet().emailAddress();
        String password = faker.internet().password(6, 10);

        registerPage.openRegisterPage();

        registerPage.submitRegistrationForm("", faker.name().lastName(), email, password, password);

        assertThat(registerPage.fieldErrorMessages()).containsExactly("Informe o nome.");
        assertThat(registerPage.currentUrl()).endsWith("/registrar");
    }

    @Test
    @DisplayName("Should reject without sobrenome")
    void shouldRejectWithoutSobrenome() {
        String email = faker.internet().emailAddress();
        String password = faker.internet().password(6, 10);

        registerPage.openRegisterPage();

        registerPage.submitRegistrationForm(faker.name().firstName(), "", email, password, password);

        assertThat(registerPage.fieldErrorMessages()).containsExactly("Informe o sobrenome.");
        assertThat(registerPage.currentUrl()).endsWith("/registrar");
    }

    @Test
    @DisplayName("Should reject without email")
    void shouldRejectWithoutEmail() {
        String password = faker.internet().password(6, 10);

        registerPage.openRegisterPage();

        registerPage.submitRegistrationForm(faker.name().firstName(), faker.name().lastName(), "", password, password);

        assertThat(registerPage.fieldErrorMessages()).containsExactly("Informe o e-mail.");
        assertThat(registerPage.currentUrl()).endsWith("/registrar");
    }

    @Test
    @DisplayName("Should reject without password")
    void shouldRejectWithoutPassword() {
        String email = faker.internet().emailAddress();
        String password = faker.internet().password(6, 10);

        registerPage.openRegisterPage();

    registerPage.submitRegistrationForm(faker.name().firstName(), faker.name().lastName(), email, "","");

        assertThat(registerPage.fieldErrorMessages()).containsExactly("Informe a senha.");
        assertThat(registerPage.currentUrl()).endsWith("/registrar");
    }
}
