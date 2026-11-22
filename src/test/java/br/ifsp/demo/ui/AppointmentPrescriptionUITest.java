package br.ifsp.demo.ui;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("UI Test")
public class AppointmentPrescriptionUITest extends BaseAppointmentDetailsUITest{

    @Test
    @DisplayName("shouldShowEmptyStateWhenThereAreNoPrescriptions")
    void shouldShowEmptyStateWhenThereAreNoPrescriptions() {
        appointmentDetailsPage.openPrescriptionTab();

        assertThat(appointmentDetailsPage.noPrescriptionsMessage())
                .isEqualTo("Nenhuma prescrição emitida neste atendimento.");
    }

    @Test
    @DisplayName("shouldEmitPrescriptionWithValidItem")
    void shouldEmitPrescriptionWithValidItem() {
        appointmentDetailsPage.openPrescriptionTab();

        String medication = faker.medical().medicineName();
        String dosage = String.valueOf(faker.number().numberBetween(1,10));
        String frequency = faker.number().numberBetween(1,3) + " vezes ao dia";
        String days = String.valueOf(faker.number().numberBetween(1,30));

        appointmentDetailsPage.fillPrescriptionItem(0, medication, dosage, "Oral", frequency,days);
        appointmentDetailsPage.submitPrescription();

        assertThat(appointmentDetailsPage.prescriptionMedicationNames())
                .containsExactly(medication);

    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    @DisplayName("shouldRejectPrescriptionMedication")
    void shouldRejectPrescriptionWithoutMedication(String medication) {

        appointmentDetailsPage.openPrescriptionTab();

        String dosage = String.valueOf(faker.number().numberBetween(1,10));
        String frequency = faker.number().numberBetween(1,3) + " vezes ao dia";
        String days = String.valueOf(faker.number().numberBetween(1,30));

        appointmentDetailsPage.fillPrescriptionItem(0, medication, dosage, "Oral", frequency,days);
        appointmentDetailsPage.submitPrescription();

        assertThat(appointmentDetailsPage.prescriptionMedicationErrorMessage())
                .isEqualTo("Obrigatório.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "0.1", ""})
    @DisplayName("shouldRejectPrescriptionDosage")
    void shouldRejectPrescriptionWithZeroDosage(String dosage) {

        appointmentDetailsPage.openPrescriptionTab();

        String medication = faker.medical().medicineName();
        String frequency = faker.number().numberBetween(1,3) + " vezes ao dia";
        String days = String.valueOf(faker.number().numberBetween(1,30));

        appointmentDetailsPage.fillPrescriptionItem(0, medication, dosage, "Oral", frequency,days);
        appointmentDetailsPage.submitPrescription();

        assertThat(appointmentDetailsPage.prescriptionDosageErrorMessage())
                .isEqualTo("Inteiro maior que zero.");
    }


    @ParameterizedTest(name = "shouldEmitPrescriptionWithRoute {0}")
    @ValueSource(strings = {
            "Oral",
            "Subcutânea",
            "Intramuscular",
            "Intravenosa",
            "Tópica",
            "Oftálmica",
            "Otológica"
    })
    @DisplayName("shouldEmitPrescriptionWithSelectedRoute")
    void shouldEmitPrescriptionWithSelectedRoute(String route) {
        appointmentDetailsPage.openPrescriptionTab();

        String medication = faker.medical().medicineName();
        String dosage = String.valueOf(faker.number().numberBetween(1,10));
        String frequency = faker.number().numberBetween(1,3) + " vezes ao dia";
        String days = String.valueOf(faker.number().numberBetween(1,30));

        appointmentDetailsPage.fillPrescriptionItem(0, medication, dosage, route, frequency,days);
        appointmentDetailsPage.submitPrescription();

        assertThat(appointmentDetailsPage.prescriptionItemText())
                .contains(route);

    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    @DisplayName("shouldRejectPrescription")
    void shouldRejectPrescription(String frequency) {

        appointmentDetailsPage.openPrescriptionTab();

        String medication = faker.medical().medicineName();
        String dosage = String.valueOf(faker.number().numberBetween(1,10));
        String days = String.valueOf(faker.number().numberBetween(1,30));

        appointmentDetailsPage.fillPrescriptionItem(0, medication, dosage, "Oral", frequency,days);
        appointmentDetailsPage.submitPrescription();

        assertThat(appointmentDetailsPage.prescriptionFrequencyErrorMessage())
                .isEqualTo("Obrigatória.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "0.1", ""})
    @DisplayName("shouldRejectDays")
    void shouldRejectDays(String days) {

        appointmentDetailsPage.openPrescriptionTab();

        String medication = faker.medical().medicineName();
        String frequency = faker.number().numberBetween(1,3) + " vezes ao dia";
        String dosage = String.valueOf(faker.number().numberBetween(1,10));

        appointmentDetailsPage.fillPrescriptionItem(0, medication, dosage , "Oral", frequency,days);
        appointmentDetailsPage.submitPrescription();

        assertThat(appointmentDetailsPage.prescriptionDaysErrorMessage())
                .isEqualTo("Inteiro maior que zero.");
    }

    @Test
    @DisplayName("shouldEmitPrescriptionWithMultipleItems")
    void shouldEmitPrescriptionWithMultipleItems() {

        appointmentDetailsPage.openPrescriptionTab();

        String firstMedication = faker.medical().medicineName();
        String secondMedication = faker.medical().medicineName();
        String dosage = String.valueOf(faker.number().numberBetween(1,10));
        String frequency = faker.number().numberBetween(1,3) + " vezes ao dia";
        String days = String.valueOf(faker.number().numberBetween(1,30));

        appointmentDetailsPage.fillPrescriptionItem(0, firstMedication, dosage, "Oral", frequency,days);
        appointmentDetailsPage.addAnotherPrescriptionItem();
        appointmentDetailsPage.fillPrescriptionItem(1, secondMedication, dosage, "Oral", frequency,days);
        appointmentDetailsPage.submitPrescription();

        assertThat(appointmentDetailsPage.prescriptionMedicationNames())
                .containsExactly(firstMedication, secondMedication);
    }
}
