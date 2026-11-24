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
}
