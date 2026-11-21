package br.ifsp.demo.ui;

import com.github.javafaker.Faker;

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
}
