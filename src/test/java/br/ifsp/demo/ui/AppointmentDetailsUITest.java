package br.ifsp.demo.ui;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@Tag("UITest")
public class AppointmentDetailsUITest extends BaseSeleniumTest{

    private AppointmentDetailsPageObject appointmentDetailsPage;

    @Override
    protected  void setInitialPage(){
        Faker faker = new Faker();

        String name = faker.name().firstName();
        String lastname = faker.name().lastName();
        String email = faker.internet().emailAddress();
        String password = faker.internet().password(6,10);

        RegisterPageObject registerPage = new RegisterPageObject(driver);
        registerPage.openRegisterPage();
        registerPage.registerAccount(name, lastname, email, password, password);

        LoginPageObject loginPage = new LoginPageObject(driver);
        loginPage.authenticate(email, password);

        AppointmentPageObject appointmentsPage = new AppointmentPageObject(driver);
        appointmentsPage.selectAppointment();
        appointmentsPage.selectNewAnimal();
        appointmentDetailsPage = appointmentsPage.clickOpenAppointment();

        appointmentDetailsPage = new AppointmentDetailsPageObject(driver);

    }

    @Test
    @DisplayName("shouldRegisterValidVitalSigns")
    void shouldRegisterValidVitalSigns() {
        appointmentDetailsPage.registerVitalSigns("38.5", "120", "10.5");

        assertThat(appointmentDetailsPage.vitalsSaveMessage()).isEqualTo("Sinais vitais salvos.");
    }

    @Test
    @DisplayName("shouldRejectZeroTemperature")
    void shouldRejectZeroTemperature() {
        appointmentDetailsPage.registerVitalSigns("0","120", "10.5");

        assertThat(appointmentDetailsPage.temperatureErrorMessage()).isEqualTo("Temperatura deve ser maior que zero.");

    }

    @Test
    @DisplayName("shouldRejectNegativeTemperature")
    void shouldRejectNegativeTemperature() {
        appointmentDetailsPage.registerVitalSigns("-1","120", "10.5");

        assertThat(appointmentDetailsPage.temperatureErrorMessage()).isEqualTo("Temperatura deve ser maior que zero.");

    }


    @Test
    @DisplayName("shouldRejectZeroHeartRate")
    void shouldRejectZeroHeartRate() {
        appointmentDetailsPage.registerVitalSigns("38","0", "10.5");

        assertThat(appointmentDetailsPage.heartRateErrorMessage()).isEqualTo("Frequência cardíaca deve ser maior que zero.");

    }
}
