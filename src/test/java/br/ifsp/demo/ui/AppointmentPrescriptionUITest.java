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
}
