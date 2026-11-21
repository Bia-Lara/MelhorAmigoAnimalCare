package br.ifsp.demo.ui;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import  static org.assertj.core.api.Assertions.*;

@Tag("UITest")
public class AppointmentsUITest extends BaseSeleniumTest {

    private  AppointmentPageObject appointmentPage;

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

        appointmentPage = new AppointmentPageObject(driver);

    }


    @Test
    @DisplayName("shouldOpenEmergencyAppointmentWithNewAnimal")
    void shouldOpenEmergencyAppointmentWithNewAnimal() {
        appointmentPage.selectAppointment();
        appointmentPage.selectNewAnimal();

        assertThat(appointmentPage.selectedAnimal())
                .isEqualTo("Novo animal");

        AppointmentDetailsPageObject detailsPage = appointmentPage.clickOpenAppointment();

        assertThat(detailsPage.appointmentId()).isNotBlank();
        assertThat(detailsPage.appointmentOrigin()).isEqualTo("Pronto atendimento");
    }

    @DisplayName("shouldOpenScheduledAppointmentWithNewAnimal")
    void shouldOpenScheduledAppointmentWithNewAnimal() {
        appointmentPage.selectScheduledAppointment();
        appointmentPage.selectNewAnimal();

        assertThat(appointmentPage.selectedAnimal())
                .isEqualTo("Novo animal");

        AppointmentDetailsPageObject detailsPage = appointmentPage.clickOpenAppointment();

        assertThat(detailsPage.appointmentId()).isNotBlank();
        assertThat(detailsPage.appointmentOrigin()).isEqualTo("Agendamento");
    }

    @Test
    @DisplayName("shouldRejectEmergencyAppointmentForAnimalAlreadyInProgress")
    void shouldRejectEmergencyAppointmentForAnimalAlreadyInProgress() {
        appointmentPage.selectAppointment();
        appointmentPage.selectNewAnimal();

        AppointmentDetailsPageObject firstAppointment = appointmentPage.clickOpenAppointment();
        String animalId = firstAppointment.animalId();
        assertThat(firstAppointment.appointmentId()).isNotBlank();

        AppointmentPageObject appointmentPage = firstAppointment.backToAppointments();

        appointmentPage.selectAppointment();
        appointmentPage.selectFirstExistingAnimal();
        appointmentPage.clickOpenAppointment();

        assertThat(appointmentPage.openingErrorMessage()).isEqualTo("Animal - "  + animalId + " já possui atendimento em andamento!");
    }

    @Test
    @DisplayName("shouldSearchExistingAppointment")
    void shouldSearchExistingAppointment() {
        appointmentPage.selectAppointment();
        appointmentPage.selectNewAnimal();

        AppointmentDetailsPageObject createdAppointment = appointmentPage.clickOpenAppointment();

        String appointmentId = createdAppointment.appointmentId();

        AppointmentPageObject appointmentsPage = createdAppointment.backToAppointments();

        appointmentsPage.enterAppointmentId(appointmentId);

        AppointmentDetailsPageObject foundAppointment = appointmentsPage.clickSearchAppointment();

        assertThat(foundAppointment.appointmentId()).isEqualTo(appointmentId);

    }


    
}
