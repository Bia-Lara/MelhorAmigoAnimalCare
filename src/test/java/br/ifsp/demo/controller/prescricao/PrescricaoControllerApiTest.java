package br.ifsp.demo.controller.prescricao;

import br.ifsp.demo.controller.BaseApiIntegrationTest;
import br.ifsp.demo.model.abertura.AnimalId;
import br.ifsp.demo.model.abertura.Atendimento;
import br.ifsp.demo.model.prescricao.ItemPrescricao;
import br.ifsp.demo.model.prescricao.Prescricao;
import br.ifsp.demo.repository.AtendimentoRepository;
import br.ifsp.demo.security.user.User;
import br.ifsp.demo.shared.EntityBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Tag("ApiTest")
@Tag("IntegrationTest")
@DisplayName("PrescricaoController API")
class PrescricaoControllerApiTest extends BaseApiIntegrationTest {

    private static final String PASSWORD = "password";

    private String token;

    @Autowired
    private AtendimentoRepository atendimentoRepository;


    @BeforeEach
    void authSetup() {
        User user = registerUser(PASSWORD);
        token = authenticate(user.getEmail(), PASSWORD);
    }

    private Atendimento givenAtendimentoEmAndamento() {
        Atendimento atendimento = Atendimento.abrirProntoAtendimento(AnimalId.of(UUID.randomUUID()));
        atendimentoRepository.salvar(atendimento);
        return atendimento;
    }

    @Nested
    @DisplayName("POST /{atendimentoId}/prescricoes")
    class Emitir {

        @Test
        @DisplayName("Should return 201 with an open prescription and the items sent")
        void shouldReturn201WithOpenPrescription() {
            UUID id = givenAtendimentoEmAndamento().getId().getValue();

            Map<String, Object> item = Map.of(
                    "medicamento", "Dipirona",
                    "dosagem", 10,
                    "via", "Oral",
                    "frequencia", "8/8h",
                    "diasDuracao", 5);

            given().contentType("application/json").port(port)
                    .header("Authorization", "Bearer " + token)
                    .body(Map.of("itens", List.of(item)))
                    .when().post("/api/v1/atendimentos/" + id + "/prescricoes")
                    .then().log().ifValidationFails(LogDetail.BODY)
                    .statusCode(201)
                    .contentType(ContentType.JSON)
                    .body("id", notNullValue())
                    .body("status", equalTo("ABERTA"))
                    .body("dataHoraEmissao", notNullValue())
                    .body("itens", hasSize(1))
                    .body("itens[0].id", notNullValue())
                    .body("itens[0].medicamento", equalTo("Dipirona"))
                    .body("itens[0].dosagem", equalTo(10))
                    .body("itens[0].via", equalTo("Oral"))
                    .body("itens[0].frequencia", equalTo("8/8h"))
                    .body("itens[0].diasDuracao", equalTo(5));
        }

        @Test
        @DisplayName("Should return 400 when item list is empty")
        void shouldReturn400WhenItemListIsEmpty() {
            UUID id = givenAtendimentoEmAndamento().getId().getValue();

            given().contentType("application/json").port(port)
                    .header("Authorization", "Bearer " + token)
                    .body(Map.of("itens", List.of()))
                    .when().post("/api/v1/atendimentos/" + id + "/prescricoes")
                    .then().log().ifValidationFails(LogDetail.BODY)
                    .statusCode(400)
                    .contentType(ContentType.JSON)
                    .body("message", containsString("pelo menos um item"));
        }
    }

    @Nested
    @DisplayName("GET /{atendimentoId}/prescricoes")
    class Listar {

        @Test
        @DisplayName("Should return 200 with the prescriptions and their items")
        void shouldReturn200WithPrescriptionsAndItems() {
            Atendimento atendimento = EntityBuilder.createCompleteAtendimento();
            ItemPrescricao item = EntityBuilder.createRandomItemPrescricao();
            Prescricao prescricao = atendimento.emitirPrescricao(List.of(item));
            atendimentoRepository.salvar(atendimento);
            UUID id = atendimento.getId().getValue();

            given().contentType("application/json").port(port)
                    .header("Authorization", "Bearer " + token)
                    .when().get("/api/v1/atendimentos/" + id + "/prescricoes")
                    .then().log().ifValidationFails(LogDetail.BODY)
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body("$", hasSize(1))
                    .body("[0].id", equalTo(prescricao.getId().getValue().toString()))
                    .body("[0].status", equalTo("ABERTA"))
                    .body("[0].itens", hasSize(1))
                    .body("[0].itens[0].medicamento", equalTo(item.getMedicamento()));
        }
    }
}