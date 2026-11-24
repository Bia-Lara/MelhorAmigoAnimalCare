package br.ifsp.demo.ui.tests;
import br.ifsp.demo.ui.base.BaseSeleniumTest;
import br.ifsp.demo.ui.pages.AppointmentPageObject;
import br.ifsp.demo.ui.pages.LoginPageObject;
import br.ifsp.demo.ui.pages.RegisterPageObject;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Tag;

@Tag("UITest")
public class AppoinmentResponsivenessUiTest extends BaseSeleniumTest {

    private AppointmentPageObject appointmentPage;

    @Override
    protected void setInitialPage() {
        Faker faker = new Faker();

        String name = faker.name().firstName();
        String lastname = faker.name().lastName();
        String email = faker.internet().emailAddress();
        String password = faker.internet().password(6, 10);

        RegisterPageObject registerPage = new RegisterPageObject(driver);
        registerPage.openRegisterPage();
        registerPage.registerAccount(name, lastname, email, password, password);

        LoginPageObject loginPage = new LoginPageObject(driver);
        loginPage.authenticate(email, password);

        appointmentPage = new AppointmentPageObject(driver);
    }
}
