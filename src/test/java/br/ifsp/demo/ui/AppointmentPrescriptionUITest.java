package br.ifsp.demo.ui;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

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

        appointmentDetailsPage.fillPrescriptionItem(medication, dosage, "Oral", frequency,days);
        appointmentDetailsPage.submitPrescription();

        assertThat(appointmentDetailsPage.prescriptionMedicationNames())
                .containsExactly(medication);

    }

    @Test
    @DisplayName("shouldRejectPrescriptionWithoutMedication")
    void shouldRejectPrescriptionWithoutMedication() {

        appointmentDetailsPage.openPrescriptionTab();

        String dosage = String.valueOf(faker.number().numberBetween(1,10));
        String frequency = faker.number().numberBetween(1,3) + " vezes ao dia";
        String days = String.valueOf(faker.number().numberBetween(1,30));

        appointmentDetailsPage.fillPrescriptionItem("", dosage, "Oral", frequency,days);
        appointmentDetailsPage.submitPrescription();

        assertThat(appointmentDetailsPage.prescriptionMedicationErrorMessage())
                .isEqualTo("Obrigatório.");
    }

    @Test
    @DisplayName("shouldRejectPrescriptionWithWhitespaceOnlyMedication")
    void shouldRejectPrescriptionWithWhitespaceOnlyMedication() {

        appointmentDetailsPage.openPrescriptionTab();

        String dosage = String.valueOf(faker.number().numberBetween(1,10));
        String frequency = faker.number().numberBetween(1,3) + " vezes ao dia";
        String days = String.valueOf(faker.number().numberBetween(1,30));

        appointmentDetailsPage.fillPrescriptionItem(" ", dosage, "Oral", frequency,days);
        appointmentDetailsPage.submitPrescription();

        assertThat(appointmentDetailsPage.prescriptionMedicationErrorMessage())
                .isEqualTo("Obrigatório.");
    }

    @Test
    @DisplayName("shouldRejectPrescriptionWithZeroDosage")
    void shouldRejectPrescriptionWithZeroDosage() {

        appointmentDetailsPage.openPrescriptionTab();

        String medication = faker.medical().medicineName();
        String frequency = faker.number().numberBetween(1,3) + " vezes ao dia";
        String days = String.valueOf(faker.number().numberBetween(1,30));

        appointmentDetailsPage.fillPrescriptionItem(medication, "0", "Oral", frequency,days);
        appointmentDetailsPage.submitPrescription();

        assertThat(appointmentDetailsPage.prescriptionDosageErrorMessage())
                .isEqualTo("Inteiro maior que zero.");
    }

    @Test
    @DisplayName("shouldRejectPrescriptionWithNegativeDosage")
    void shouldRejectPrescriptionWithNegativeDosage() {

        appointmentDetailsPage.openPrescriptionTab();

        String medication = faker.medical().medicineName();
        String frequency = faker.number().numberBetween(1,3) + " vezes ao dia";
        String days = String.valueOf(faker.number().numberBetween(1,30));

        appointmentDetailsPage.fillPrescriptionItem(medication, "-1", "Oral", frequency,days);
        appointmentDetailsPage.submitPrescription();

        assertThat(appointmentDetailsPage.prescriptionDosageErrorMessage())
                .isEqualTo("Inteiro maior que zero.");
    }

    @Test
    @DisplayName("shouldRejectPrescriptionWithDecimalDosage")
    void shouldRejectPrescriptionWithDecimalDosage() {

        appointmentDetailsPage.openPrescriptionTab();

        String medication = faker.medical().medicineName();
        String frequency = faker.number().numberBetween(1,3) + " vezes ao dia";
        String days = String.valueOf(faker.number().numberBetween(1,30));

        appointmentDetailsPage.fillPrescriptionItem(medication, "0.1", "Oral", frequency,days);
        appointmentDetailsPage.submitPrescription();

        assertThat(appointmentDetailsPage.prescriptionDosageErrorMessage())
                .isEqualTo("Inteiro maior que zero.");
    }


}
