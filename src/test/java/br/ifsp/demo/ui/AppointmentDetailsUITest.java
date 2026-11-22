package br.ifsp.demo.ui;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static org.assertj.core.api.Assertions.*;

@Tag("UITest")
public class AppointmentDetailsUITest extends BaseSeleniumTest{

    private AppointmentDetailsPageObject appointmentDetailsPage;
    Faker faker = new Faker();

    @Override
    protected  void setInitialPage(){

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
        appointmentDetailsPage.registerVitalSigns("38.5",
                String.valueOf(faker.number().randomNumber()),
                String.valueOf(faker.number().randomDouble(2,1, 40)));

        assertThat(appointmentDetailsPage.vitalsSaveMessage()).isEqualTo("Sinais vitais salvos.");
    }

    @Test
    @DisplayName("shouldRejectZeroTemperature")
    void shouldRejectZeroTemperature() {
        appointmentDetailsPage.registerVitalSigns("0",
                String.valueOf(faker.number().randomNumber()),
                String.valueOf(faker.number().randomDouble(2,1, 40)));

        assertThat(appointmentDetailsPage.temperatureErrorMessage()).isEqualTo("Temperatura deve ser maior que zero.");

    }

    @Test
    @DisplayName("shouldRejectNegativeTemperature")
    void shouldRejectNegativeTemperature() {
        appointmentDetailsPage.registerVitalSigns("-1",
                String.valueOf(faker.number().randomNumber()),
                String.valueOf(faker.number().randomDouble(2,1, 40)));

        assertThat(appointmentDetailsPage.temperatureErrorMessage()).isEqualTo("Temperatura deve ser maior que zero.");

    }


    @Test
    @DisplayName("shouldRejectZeroHeartRate")
    void shouldRejectZeroHeartRate() {
        appointmentDetailsPage.registerVitalSigns(String.valueOf(
                faker.number().randomDouble(1,35, 40)),
                "0",
                String.valueOf(faker.number().randomDouble(2,1, 40)));

        assertThat(appointmentDetailsPage.heartRateErrorMessage()).isEqualTo("Frequência cardíaca deve ser maior que zero.");

    }

    @Test
    @DisplayName("shouldRejectNegativeHeartRate")
    void shouldRejectNegativeHeartRate() {
        appointmentDetailsPage.registerVitalSigns(String.valueOf(
                faker.number().randomDouble(1,35, 40)),
                "-1",
                String.valueOf(faker.number().randomDouble(2,1, 40)));

        assertThat(appointmentDetailsPage.heartRateErrorMessage()).isEqualTo("Frequência cardíaca deve ser maior que zero.");

    }


    @Test
    @DisplayName("shouldRejectDecimalHeartRate")
    void shouldRejectDecimalHeartRate() {
        Faker faker = new Faker();
        appointmentDetailsPage.registerVitalSigns(
                String.valueOf(faker.number().randomDouble(1,35, 40)),
                "120.5",
                String.valueOf(faker.number().randomDouble(2,1, 40)));

        assertThat(appointmentDetailsPage.heartRateErrorMessage()).isEqualTo("Use um número inteiro (bpm).");

    }

    @Test
    @DisplayName("shouldRejectZeroWeight")
    void shouldRejectZeroWeight() {
        appointmentDetailsPage.registerVitalSigns(String.valueOf(faker.number().randomDouble(1,35, 40)),
                String.valueOf(faker.number().randomNumber()), "0");

        assertThat(appointmentDetailsPage.weightErrorMessage()).isEqualTo("Peso deve ser maior que zero.");

    }

    @Test
    @DisplayName("shouldUpdateExistingVitalSigns")
    void shouldUpdateExistingVitalSigns() {
        String updatedTemperature = String.valueOf(faker.number().randomDouble(1,38,40));
        String updatedHeartRate = String.valueOf(faker.number().randomNumber());
        String updatedWeight = String.valueOf(faker.number().randomDouble(2,20,40));

        appointmentDetailsPage.registerVitalSigns(
                String.valueOf(faker.number().randomDouble(1,38,40)),
                String.valueOf(faker.number().randomNumber()),
                String.valueOf(faker.number().randomDouble(2,20,40))
        );

        assertThat(appointmentDetailsPage.vitalsSaveMessage()).isEqualTo("Sinais vitais salvos.");
        assertThat(appointmentDetailsPage.vitalsSubmitButtonText()).isEqualTo("Atualizar");

        appointmentDetailsPage.updateVitalSigns(updatedTemperature, updatedHeartRate, updatedWeight);

        assertThat(appointmentDetailsPage.temperatureValue()).isEqualTo(updatedTemperature);
        assertThat(appointmentDetailsPage.heartValue()).isEqualTo(updatedHeartRate);
        assertThat(appointmentDetailsPage.weightValue()).isEqualTo(updatedWeight);

    }


    @Test
    @DisplayName("shouldRejectEmptyChiefComplaint")
    void shouldRejectEmptyChiefComplaint() {
        appointmentDetailsPage.registerAnmnesis("", faker.lorem().sentence());

        assertThat(appointmentDetailsPage.complaintErrorMessage())
                .isEqualTo("A queixa principal é obrigatória.");
    }

    @Test
    @DisplayName("shouldRejectEmptyClinicalHistory")
    void shouldRejectEmptyClinicalHistory() {
        appointmentDetailsPage.registerAnmnesis(faker.lorem().sentence(), "");

        assertThat(appointmentDetailsPage.historyErrorMessage())
                .isEqualTo("O histórico clínico é obrigatório.");
    }

    @Test
    @DisplayName("shouldAcceptChiefComplaintAtMaximumLength")
    void shouldAcceptChiefComplaintAtMaximumLength() {
        String complaint = "A".repeat(100);

        appointmentDetailsPage.registerAnmnesis(complaint, faker.lorem().sentence());

        assertThat(appointmentDetailsPage.anamneseSavedMessage())
                .isEqualTo("Anamnese salva.");

    }

    @Test
    @DisplayName("shouldRejectChiefComplaintAboveMaximumLenght")
    void shouldRejectChiefComplaintAboveMaximumLenght() {
        String complaint = "A".repeat(101);

        appointmentDetailsPage.registerAnmnesis(complaint, faker.lorem().sentence());

        assertThat(appointmentDetailsPage.complaintErrorMessage())
                .isEqualTo("Máximo de 100 caracteres.");

    }

    @Test
    @DisplayName("shouldRejectClinicalHistoryAboveMaximumLenght")
    void shouldRejectClinicalHistoryAboveMaximumLenght() {
        String history = "A".repeat(501);

        appointmentDetailsPage.registerAnmnesis(faker.lorem().sentence(), history);

        assertThat(appointmentDetailsPage.historyErrorMessage())
                .isEqualTo("Máximo de 500 caracteres.");
    }


    @Test
    @DisplayName("shouldAcceptClinicalHistoryAtMaximumLength")
    void shouldAcceptClinicalHistoryAtMaximumLength() {
        String history = "A".repeat(500);

        appointmentDetailsPage.registerAnmnesis(faker.lorem().sentence(), history);

        assertThat(appointmentDetailsPage.anamneseSavedMessage())
                .isEqualTo("Anamnese salva.");
    }

    @Test
    @DisplayName("shouldUpdateExistingAnamnesis")
    void shouldUpdateExistingAnamnesis() {
        String updateComplaint = faker.lorem().sentence();
        String updateHistory = faker.lorem().sentence();

        appointmentDetailsPage.registerAnmnesis(
                faker.lorem().sentence(),
                faker.lorem().sentence()
        );

        assertThat(appointmentDetailsPage.anamneseSavedMessage()).isEqualTo("Anamnese salva.");
        assertThat(appointmentDetailsPage.anamnesisSubmitButtonText()).isEqualTo("Atualizar");

        appointmentDetailsPage.updateAnamnesis(updateComplaint, updateHistory);

        assertThat(appointmentDetailsPage.complaintValue()).isEqualTo(updateComplaint);
        assertThat(appointmentDetailsPage.historyValue()).isEqualTo(updateHistory);

    }

    @Test
    @DisplayName("shouldAddValidDiagnosis")
    void shouldAddValidDiagnosis() {
        String code = faker.number().digits(3);
        String description = faker.lorem().sentence();
        String type = "Presuntivo";

        appointmentDetailsPage.addDiagnosis(code,description,type);

        assertThat(appointmentDetailsPage.hasDiagnosis(code,description,type))
                .isTrue();

        assertThat(appointmentDetailsPage.isDiagnosisRequirementComplete())
                .isTrue();

    }

    @Test
    @DisplayName("shouldRejectEmptyDiagnosisCode")
    void shouldRejectEmptyDiagnosisCode() {
        appointmentDetailsPage.addDiagnosis("", faker.lorem().sentence(), "Presuntivo");

        assertThat(appointmentDetailsPage.diagnosisCodeErrorMessage())
                .isEqualTo("O código é obrigatório.");

    }

    @Test
    @DisplayName("shouldRejectEmptyDiagnosisDescription")
    void shouldRejectEmptyDiagnosisDescription() {
        appointmentDetailsPage.addDiagnosis(faker.lorem().sentence(), "", "Presuntivo");

        assertThat(appointmentDetailsPage.diagnosisDescriptionErrorMessage())
                .isEqualTo("A descrição é obrigatória.");

    }

    @Test
    @DisplayName("shouldRejectWhitespaceOnlyDiagnosisCode")
    void shouldRejectWhitespaceOnlyDiagnosisCode() {
        appointmentDetailsPage.addDiagnosis(" ", faker.lorem().sentence(), "Presuntivo");

        assertThat(appointmentDetailsPage.diagnosisCodeErrorMessage())
                .isEqualTo("O código é obrigatório.");

    }

    @Test
    @DisplayName("shouldRejectWhitespaceOnlyDiagnosisDescription")
    void shouldRejectWhitespaceOnlyDiagnosisDescription() {
        appointmentDetailsPage.addDiagnosis(faker.lorem().sentence(), " ", "Presuntivo");

        assertThat(appointmentDetailsPage.diagnosisDescriptionErrorMessage())
                .isEqualTo("A descrição é obrigatória.");
    }

    @Test
    @DisplayName("shouldAcceptDiagnosisCodeAtMaximumLenght")
    void shouldAcceptDiagnosisCodeAtMaximumLenght() {
        String code = faker.number().digits(10);
        String description = faker.lorem().sentence();
        String type = "Presuntivo";
        appointmentDetailsPage.addDiagnosis(code, description, type);

        assertThat(appointmentDetailsPage.hasDiagnosis(code, description,type))
                .isTrue();

    }

    @Test
    @DisplayName("shouldRejectDiagnosisCodeAboveMaximumLenght")
    void shouldRejectDiagnosisCodeMaximumLenght() {
        String code = faker.number().digits(11);
        String description = faker.lorem().sentence();
        String type = "Presuntivo";
        appointmentDetailsPage.addDiagnosis(code, description, type);

        assertThat(appointmentDetailsPage.diagnosisCodeErrorMessage())
                .isEqualTo("Máximo de 10 caracteres.");

    }

    @Test
    @DisplayName("shouldAcceptDiagnosisDescriptionAtMaximumLenght")
    void shouldAcceptDiagnosisDescriptionAtMaximumLenght() {
        String code = faker.number().digits(10);
        String description = "A".repeat(200);;
        String type = "Presuntivo";
        appointmentDetailsPage.addDiagnosis(code, description, type);

        assertThat(appointmentDetailsPage.hasDiagnosis(code, description,type))
                .isTrue();

    }


    @Test
    @DisplayName("shouldAcceptDiagnosisDescriptionAboveMaximumLenght")
    void shouldAcceptDiagnosisDescriptionAboveMaximumLenght() {
        String code = faker.number().digits(10);
        String description = "A".repeat(201);;
        String type = "Presuntivo";
        appointmentDetailsPage.addDiagnosis(code, description, type);

        assertThat(appointmentDetailsPage.diagnosisDescriptionErrorMessage())
                .isEqualTo("Máximo de 200 caracteres.");

    }

    @Test
    @DisplayName("shouldAddEmpiricDiagnosis")
    void shouldAddEmpiricDiagnosis() {
        String code = faker.number().digits(10);
        String description = faker.lorem().sentence();
        String type = "Empírico";
        appointmentDetailsPage.addDiagnosis(code, description, type);

        assertThat(appointmentDetailsPage.hasDiagnosis(code, description,type))
                .isTrue();
    }

    @Test
    @DisplayName("shouldAddDefinitiveDiagnosis")
    void shouldAddDefinitiveDiagnosis() {
        String code = faker.number().digits(10);
        String description = faker.lorem().sentence();
        String type = "Definitivo";
        appointmentDetailsPage.addDiagnosis(code, description, type);

        assertThat(appointmentDetailsPage.hasDiagnosis(code, description,type))
                .isTrue();
    }

    @Test
    @DisplayName("shouldDisableClinicalFieldAfterCancellation")
    void shouldDisableClinicalFieldAfterCancellation() {
        appointmentDetailsPage.cancelAppointment(faker.lorem().sentence());

        assertThat(appointmentDetailsPage.clinicalFieldAreDisaled()).isTrue();
        assertThat(appointmentDetailsPage.isDiagnosisFormDisplayed()).isFalse();
        
    }

    @Test
    @DisplayName("shouldConcludeAppointmentWhenRequirementsAreMet")
    void shouldConcludeAppointmentWhenRequirementsAreMet() {
        appointmentDetailsPage.registerVitalSigns(String.valueOf(faker.number().randomDouble(1,35,40)),
                String.valueOf(faker.number().randomNumber()),
                String.valueOf(faker.number().randomDouble(2,1, 40)));

        assertThat(appointmentDetailsPage.vitalsSaveMessage()).isEqualTo("Sinais vitais salvos.");

        appointmentDetailsPage.registerAnmnesis(faker.lorem().sentence(), faker.lorem().sentence());
        assertThat(appointmentDetailsPage.anamneseSavedMessage()).isEqualTo("Anamnese salva.");

        String code = faker.number().digits(3);
        String description = faker.lorem().sentence();
        String type = "Presuntivo";

        appointmentDetailsPage.addDiagnosis(code,description,type);

        assertThat(appointmentDetailsPage.hasDiagnosis(code,description,type))
                .isTrue();

        assertThat(appointmentDetailsPage.isConcludeAppointmentButtonEnabled())
                .isTrue();

        appointmentDetailsPage.concludeAppointment();
        assertThat(appointmentDetailsPage.isAppointmentConcluded()).isTrue();

    }

    @Test
    @DisplayName("shouldDisableClinicalFieldsAfterConclusion")
    void shouldDisableClinicalFieldsAfterConclusion() {
        appointmentDetailsPage.registerVitalSigns(String.valueOf(faker.number().randomDouble(1,35,40)),
                String.valueOf(faker.number().randomNumber()),
                String.valueOf(faker.number().randomDouble(2,1, 40)));

        assertThat(appointmentDetailsPage.vitalsSaveMessage()).isEqualTo("Sinais vitais salvos.");

        appointmentDetailsPage.registerAnmnesis(faker.lorem().sentence(), faker.lorem().sentence());
        assertThat(appointmentDetailsPage.anamneseSavedMessage()).isEqualTo("Anamnese salva.");

        String code = faker.number().digits(3);
        String description = faker.lorem().sentence();
        String type = "Presuntivo";

        appointmentDetailsPage.addDiagnosis(code,description,type);

        assertThat(appointmentDetailsPage.hasDiagnosis(code,description,type))
                .isTrue();

        assertThat(appointmentDetailsPage.isConcludeAppointmentButtonEnabled())
                .isTrue();

        appointmentDetailsPage.concludeAppointment();

        assertThat(appointmentDetailsPage.isAppointmentConcluded()).isTrue();
        assertThat(appointmentDetailsPage.clinicalFieldAreDisaled()).isTrue();
        assertThat(appointmentDetailsPage.isDiagnosisFormDisplayed()).isFalse();
    }


    @Test
    @DisplayName("shouldHideExamRequestFormAfterConclusion")
    void shouldHideExamRequestFormAfterConclusion() {
        appointmentDetailsPage.registerVitalSigns(String.valueOf(faker.number().randomDouble(1,35,40)),
                String.valueOf(faker.number().randomNumber()),
                String.valueOf(faker.number().randomDouble(2,1, 40)));

        appointmentDetailsPage.registerAnmnesis(faker.lorem().sentence(), faker.lorem().sentence());

        String code = faker.number().digits(3);
        String description = faker.lorem().sentence();
        String type = "Presuntivo";

        appointmentDetailsPage.addDiagnosis(code,description,type);
        appointmentDetailsPage.concludeAppointment();
        appointmentDetailsPage.openExamsTab();

        assertThat(appointmentDetailsPage.isExamRequestFormDisplayed()).isFalse();

    }

    @Test
    @DisplayName("shouldFillSingleExamRequest")
    void shouldFillSingleExamRequest() {
        appointmentDetailsPage.openExamsTab();

        String name = faker.lorem().word();
        LocalDate date= LocalDate.now().plusDays(1);
        String expectedDate = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.forLanguageTag("pt-BR")));
        String selectedTime = "09:00";

        appointmentDetailsPage.fillExamRequestAt(0, name, date, selectedTime);

        assertThat(appointmentDetailsPage.examNameValue()).isEqualTo(name);
        assertThat(appointmentDetailsPage.examDateValue()).isEqualTo(expectedDate);
        assertThat(appointmentDetailsPage.examTimeValue()).isEqualTo(selectedTime);

    }

    @Test
    @DisplayName("shouldAddAnotherExamRequestRow")
    void shouldAddAnotherExamRequestRow() {
        appointmentDetailsPage.openExamsTab();

        String name = faker.lorem().word();
        LocalDate date = LocalDate.now().plusDays(1);
        LocalTime time = LocalTime.of(9,0);

        appointmentDetailsPage.fillExamRequestAt(0, name, date, time.format(DateTimeFormatter.ofPattern("HH:mm")));

        assertThat(appointmentDetailsPage.examRequestRowCount()).isEqualTo(1);

        appointmentDetailsPage.addAnotherExam();

        assertThat(appointmentDetailsPage.examRequestRowCount()).isEqualTo(2);
        assertThat(appointmentDetailsPage.examNameValue()).isEqualTo(name);
        assertThat(appointmentDetailsPage.isRemoveExamButtonDisplayed()).isTrue();
        
    }

    @Test
    @DisplayName("shouldSubmitSingleExamRequest")
    void shouldSubmitSingleExamRequest() {
        appointmentDetailsPage.openExamsTab();

        String name = faker.lorem().word();
        LocalDate date = LocalDate.now().plusDays(1);
        String time = LocalTime.of(9,0).format(DateTimeFormatter.ofPattern("HH:mm"));

        appointmentDetailsPage.fillExamRequestAt(0, name, date, time);
        appointmentDetailsPage.submitExamRequest();

        assertThat(appointmentDetailsPage.requestedExamNames()).contains(name);

    }

    @Test
    @DisplayName("shouldRejectEmptyExamResult")
    void shouldRejectEmptyExamResult() {
        appointmentDetailsPage.openExamsTab();

        String name = faker.lorem().word();
        LocalDate date = LocalDate.now().plusDays(1);
        String time = LocalTime.of(9,0).format(DateTimeFormatter.ofPattern("HH:mm"));

        appointmentDetailsPage.fillExamRequestAt(0, name, date, time);
        appointmentDetailsPage.submitExamRequest();
        appointmentDetailsPage.submitExamResult("");

        assertThat(appointmentDetailsPage.examResultErrorMessage())
                .isEqualTo("O resultado deve ser informado.");

    }

    @Test
    @DisplayName("shouldRejectExamResultWithoutOnlySpace")
    void shouldRejectExamResultWithoutOnlySpaces() {
        appointmentDetailsPage.openExamsTab();

        String name = faker.lorem().word();
        LocalDate date = LocalDate.now().plusDays(1);
        String time = LocalTime.of(9,0).format(DateTimeFormatter.ofPattern("HH:mm"));

        appointmentDetailsPage.fillExamRequestAt(0, name, date, time);
        appointmentDetailsPage.submitExamRequest();
        appointmentDetailsPage.submitExamResult(" ");

        assertThat(appointmentDetailsPage.examResultErrorMessage())
                .isEqualTo("O resultado deve ser informado.");

    }

    @Test
    @DisplayName("shouldTrimExamResult")
    void shouldTrimExamResult() {
        appointmentDetailsPage.openExamsTab();

        String name = faker.lorem().word();
        LocalDate date = LocalDate.now().plusDays(1);
        String time = LocalTime.of(9,0).format(DateTimeFormatter.ofPattern("HH:mm"));
        String result = " " + faker.lorem().sentence() + " ";
        appointmentDetailsPage.fillExamRequestAt(0, name, date, time);
        appointmentDetailsPage.submitExamRequest();
        appointmentDetailsPage.submitExamResult(result);

        assertThat(appointmentDetailsPage.registeredExamResult())
                .isEqualTo(result.trim());

    }

    @Test
    @DisplayName("shouldRegisterExamResult")
    void shouldRegisterExamResult() {
        appointmentDetailsPage.openExamsTab();

        String name = faker.lorem().word();
        LocalDate date = LocalDate.now().plusDays(1);
        String time = LocalTime.of(9,0).format(DateTimeFormatter.ofPattern("HH:mm"));
        String result = faker.lorem().sentence();

        appointmentDetailsPage.fillExamRequestAt(0, name, date, time);
        appointmentDetailsPage.submitExamRequest();
        appointmentDetailsPage.submitExamResult(result);

        assertThat(appointmentDetailsPage.registeredExamResult())
                .isEqualTo(result);

        assertThat(appointmentDetailsPage.isExamResultFormDisplayed())
                .isFalse();

    }

    @Test
    @DisplayName("shouldRemoveExamBeforeRequesting")
    void shouldRemoveExamBeforeRequesting() {
        appointmentDetailsPage.openExamsTab();

        String firstExam = faker.lorem().word();
        String removedExam = faker.lorem().word();
        LocalDate date = LocalDate.now().plusDays(1);
        String time = LocalTime.of(9,0).format(DateTimeFormatter.ofPattern("HH:mm"));
        String result = faker.lorem().sentence();

        appointmentDetailsPage.fillExamRequestAt(0, firstExam, date, time);
        appointmentDetailsPage.addAnotherExam();
        appointmentDetailsPage.fillExamRequestAt(1, removedExam, date, time);

        appointmentDetailsPage.removeExamRequestAt(1);

        assertThat(appointmentDetailsPage.examRequestRowCount()).isEqualTo(1);

        appointmentDetailsPage.submitExamRequest();

        assertThat(appointmentDetailsPage.requestedExamNames())
                .containsExactly(firstExam);

    }

    @Test
    @DisplayName("shouldRejectExamRequestWithoutName")
    void shouldRejectExamRequestWithoutName() {
        appointmentDetailsPage.openExamsTab();

        LocalDate date = LocalDate.now().plusDays(1);
        String time = LocalTime.of(9,0).format(DateTimeFormatter.ofPattern("HH:mm"));
        appointmentDetailsPage.fillExamRequestAt(0, "", date, time);
        appointmentDetailsPage.submitExamRequest();

        assertThat(appointmentDetailsPage.examNameErrorMessage())
                .isEqualTo("Informe o nome do exame.");

    }

    @Test
    @DisplayName("shouldTrimExamName")
    void shouldTrimExamName() {
        appointmentDetailsPage.openExamsTab();

        String name = " " + faker.lorem().sentence() + " ";
        LocalDate date = LocalDate.now().plusDays(1);
        String time = LocalTime.of(9,0).format(DateTimeFormatter.ofPattern("HH:mm"));
        appointmentDetailsPage.fillExamRequestAt(0, name, date, time);
        appointmentDetailsPage.submitExamRequest();

        assertThat(appointmentDetailsPage.requestedExamNames())
                .containsExactly(name.trim());

    }

    @Test
    @DisplayName("shouldRejectExamRequestWithoutDateAndTime")
    void shouldRejectExamRequestWithoutDateAndTime() {
        appointmentDetailsPage.openExamsTab();

        appointmentDetailsPage.enterExamNameAt(0, faker.lorem().word());
        appointmentDetailsPage.submitExamRequest();

        assertThat(appointmentDetailsPage.examDateErrorMessage())
                .isEqualTo("Informe data e hora.");

    }

    @Test
    @DisplayName("shouldRejectExamRequestsWithoutTime")
    void shouldRejectExamRequestsWithoutTime() {
        appointmentDetailsPage.openExamsTab();

        LocalDate date = LocalDate.now().plusDays(1);
        appointmentDetailsPage.fillExamRequestAt(0, "", date, "");
        appointmentDetailsPage.submitExamRequest();

        assertThat(appointmentDetailsPage.examDateErrorMessage())
                .isEqualTo("Selecione o horário.");

    }

    @Test
    @DisplayName("shouldRejectExamRequestsWithoutDate")
    void shouldRejectExamRequestsWithoutDate() {
        appointmentDetailsPage.openExamsTab();


        String time = LocalTime.of(18,0).format(DateTimeFormatter.ofPattern("HH:mm"));
        appointmentDetailsPage.enterExamNameAt(0, faker.lorem().word());
        appointmentDetailsPage.selectExamTimeAt(0, time);
        appointmentDetailsPage.submitExamRequest();

        assertThat(appointmentDetailsPage.examDateErrorMessage())
                .isEqualTo("Selecione a data.");

    }

    @Test
    @DisplayName("shouldSubmitMultipleExamRequests")
    void shouldSubmitMultipleExamRequests() {
        appointmentDetailsPage.openExamsTab();

        String firstExam = faker.lorem().word();
        String secondExam = faker.lorem().word();
        LocalDate date= LocalDate.now().plusDays(1);
        String time = LocalTime.of(9,0).format(DateTimeFormatter.ofPattern("HH:mm"));

        appointmentDetailsPage.fillExamRequestAt(0,firstExam,date,time);
        appointmentDetailsPage.addAnotherExam();
        appointmentDetailsPage.fillExamRequestAt(1, secondExam, date, time);
        appointmentDetailsPage.submitExamRequest();

        assertThat(appointmentDetailsPage.requestedExamNames())
                .containsExactly(firstExam, secondExam);

    }

    @Test
    @DisplayName("shouldNotAllowPastExamDate")
    void shouldNotAllowPastExamDate() {
        appointmentDetailsPage.openExamsTab();

        LocalDate yesterday = LocalDate.now().minusDays(1);

        assertThat(appointmentDetailsPage.isExamDateSelectable(0, yesterday))
                .isFalse();
    }

    @Test
    @DisplayName("shouldShowEmptyStateWhenThereAreNoRequestedExams")
    void shouldShowEmptyStateWhenThereAreNoRequestedExams() {
        appointmentDetailsPage.openExamsTab();

        assertThat(appointmentDetailsPage.noRequestedExamsMessage())
                .isEqualTo("Nenhum exame solicitado.");

    }
}
