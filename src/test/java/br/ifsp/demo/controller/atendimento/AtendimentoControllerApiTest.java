package br.ifsp.demo.controller.atendimento;

import br.ifsp.demo.controller.BaseApiIntegrationTest;
import br.ifsp.demo.model.abertura.AnimalId;
import br.ifsp.demo.model.abertura.Atendimento;
import br.ifsp.demo.model.abertura.AtendimentoId;
import br.ifsp.demo.model.abertura.StatusAtendimento;
import br.ifsp.demo.model.clinico.SinaisVitais;
import br.ifsp.demo.repository.AtendimentoRepository;
import br.ifsp.demo.security.user.User;
import br.ifsp.demo.shared.EntityBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Tag("ApiTest")
@Tag("IntegrationTest")
@DisplayName("Atendimento Controller API")
class AtendimentoControllerApiTest extends BaseApiIntegrationTest {

    private static final String PASSWORD = "password";

    private String token;

    @Autowired
    private AtendimentoRepository atendimentoRepository;

    private Atendimento givenAtendimentoEmAndamento() {
        Atendimento atendimento = Atendimento.abrirProntoAtendimento(AnimalId.of(UUID.randomUUID()));
        atendimentoRepository.salvar(atendimento);
        return atendimento;
    }

    @BeforeEach
    void authSetup() {
        User user = registerUser(PASSWORD);
        token = authenticate(user.getEmail(), PASSWORD);
    }

    @Nested
    @DisplayName("POST /pronto-atendimento")
    class ProntoAtendimento {

        @Test
        @DisplayName("Should return 201 and body when animalId is valid")
        void shouldReturn201WithLocationAndBody() {
            UUID animalId = UUID.randomUUID();

            given().contentType("application/json").port(port)
                    .header("Authorization", "Bearer " + token)
                    .body(Map.of("animalId", animalId))
                    .when().post("/api/v1/atendimentos/pronto-atendimento")
                    .then().log().ifValidationFails(LogDetail.BODY)
                    .statusCode(201)
                    .body("id", notNullValue());
        }

        @Test
        @DisplayName("Should return 400 with message when animalId is null")
        void shouldReturn400WhenAnimalIdIsNull() {
            given().contentType("application/json").port(port)
                    .header("Authorization", "Bearer " + token)
                    .body(Map.of())
                    .when().post("/api/v1/atendimentos/pronto-atendimento")
                    .then().log().ifValidationFails(LogDetail.BODY)
                    .statusCode(400)
                    .body("message", containsString("animalId"));
        }

        @Test
        @DisplayName("Should return 409 when animal already has an atendimento in progress")
        void shouldReturn409WhenAnimalAlreadyInAtendimento() {
            UUID id = UUID.randomUUID();
            AnimalId animalId = AnimalId.of(id);
            atendimentoRepository.salvar(Atendimento.abrirProntoAtendimento(animalId));

            given().contentType("application/json").port(port)
                    .header("Authorization", "Bearer " + token)
                    .body(Map.of("animalId", id))
                    .when().post("/api/v1/atendimentos/pronto-atendimento")
                    .then().log().ifValidationFails(LogDetail.BODY)
                    .statusCode(409)
                    .body("message", not(emptyOrNullString()));
        }
    }

    @Nested
    @DisplayName("POST /agendados")
    class Agendados {

        @Test
        @DisplayName("Should return 201 with body when animalId and agendamentoId are valid")
        void shouldReturn201WhenRequestIsValid() {
            UUID animalId = UUID.randomUUID();
            UUID agendamentoId = UUID.randomUUID();

            given().contentType("application/json").port(port)
                    .header("Authorization", "Bearer " + token)
                    .body(Map.of("animalId", animalId, "agendamentoId", agendamentoId))
                    .when().post("/api/v1/atendimentos/agendados")
                    .then().log().ifValidationFails(LogDetail.BODY)
                    .statusCode(201)
                    .body("id", notNullValue())
                    .body("animalId", equalTo(animalId.toString()))
                    .body("agendamentoId", equalTo(agendamentoId.toString()));
        }

        @Test
        @DisplayName("Should return 400 when animalId is null")
        void shouldReturn400WhenAnimalIdIsNull() {
            given().contentType("application/json").port(port)
                    .header("Authorization", "Bearer " + token)
                    .body(Map.of("agendamentoId", UUID.randomUUID()))
                    .when().post("/api/v1/atendimentos/agendados")
                    .then().log().ifValidationFails(LogDetail.BODY)
                    .statusCode(400)
                    .body("message", containsString("animalId"));
        }

        @Test
        @DisplayName("Should return 400 when agendamentoId is null")
        void shouldReturn400WhenAgendamentoIdIsNull() {
            given().contentType("application/json").port(port)
                    .header("Authorization", "Bearer " + token)
                    .body(Map.of("animalId", UUID.randomUUID()))
                    .when().post("/api/v1/atendimentos/agendados")
                    .then().log().ifValidationFails(LogDetail.BODY)
                    .statusCode(400)
                    .body("message", containsString("agendamentoId"));
        }

        @Test
        @DisplayName("Should return 400 when agendamentoId is not a valid UUID")
        void shouldReturn400WhenAgendamentoIdIsInvalidUuid() {
            given().contentType("application/json").port(port)
                    .header("Authorization", "Bearer " + token)
                    .body(Map.of("animalId", UUID.randomUUID(), "agendamentoId", "not-a-uuid"))
                    .when().post("/api/v1/atendimentos/agendados")
                    .then().log().ifValidationFails(LogDetail.BODY)
                    .statusCode(400)
                    .body("message", not(emptyOrNullString()));
        }
    }

    @Nested
    @DisplayName("POST /{id}/cancelamento")
    class Cancelamento {

        @Test
        @DisplayName("Should return 200 with CANCELADO status and justificativa")
        void shouldReturn200WhenJustificativaIsProvided() {
            Atendimento atendimento = givenAtendimentoEmAndamento();
            UUID id = atendimento.getId().getValue();

            given().contentType("application/json").port(port)
                    .header("Authorization", "Bearer " + token)
                    .body(Map.of("justificativa", "Tutor desistiu do atendimento"))
                    .when().post("/api/v1/atendimentos/" + id + "/cancelamento")
                    .then().log().ifValidationFails(LogDetail.BODY)
                    .statusCode(200)
                    .body("id", equalTo(id.toString()))
                    .body("status", equalTo("CANCELADO"))
                    .body("justificativa", equalTo("Tutor desistiu do atendimento"));
        }

        @Test
        @DisplayName("Should return 404 when atendimento does not exist")
        void shouldReturn404WhenAtendimentoDoesNotExist() {
            given().contentType("application/json").port(port)
                    .header("Authorization", "Bearer " + token)
                    .body(Map.of("justificativa", "Tutor desistiu do atendimento"))
                    .when().post("/api/v1/atendimentos/" + UUID.randomUUID() + "/cancelamento")
                    .then().log().ifValidationFails(LogDetail.BODY)
                    .statusCode(404)
                    .contentType(ContentType.JSON)
                    .body("message", not(emptyOrNullString()));
        }

        @Test
        @DisplayName("Should return 409 when atendimento is already cancelled")
        void shouldReturn409WhenAtendimentoIsAlreadyCancelled() {
            Atendimento atendimento = Atendimento.abrirProntoAtendimento(AnimalId.of(UUID.randomUUID()));
            atendimento.cancelar("Tutor desistiu do atendimento");
            atendimentoRepository.salvar(atendimento);
            UUID id = atendimento.getId().getValue();

            given().contentType("application/json").port(port)
                    .header("Authorization", "Bearer " + token)
                    .body(Map.of("justificativa", "Segunda tentativa de cancelamento"))
                    .when().post("/api/v1/atendimentos/" + id + "/cancelamento")
                    .then().log().ifValidationFails(LogDetail.BODY)
                    .statusCode(409)
                    .contentType(ContentType.JSON)
                    .body("message", not(emptyOrNullString()));
        }

        @Test
        @DisplayName("Should return 400 when justificativa is null")
        void shouldReturn400WhenJustificativaIsNull() {
            UUID id = givenAtendimentoEmAndamento().getId().getValue();

            given().contentType("application/json").port(port)
                    .header("Authorization", "Bearer " + token)
                    .body(Map.of())
                    .when().post("/api/v1/atendimentos/" + id + "/cancelamento")
                    .then().log().ifValidationFails(LogDetail.BODY)
                    .statusCode(400)
                    .contentType(ContentType.JSON)
                    .body("message", not(emptyOrNullString()));
        }

        @Test
        @DisplayName("Should return 400 when justificativa is blank")
        void shouldReturn400WhenJustificativaIsBlank() {
            UUID id = givenAtendimentoEmAndamento().getId().getValue();

            given().contentType("application/json").port(port)
                    .header("Authorization", "Bearer " + token)
                    .body(Map.of("justificativa", " "))
                    .when().post("/api/v1/atendimentos/" + id + "/cancelamento")
                    .then().log().ifValidationFails(LogDetail.BODY)
                    .statusCode(400)
                    .contentType(ContentType.JSON)
                    .body("message", not(emptyOrNullString()));
        }

        @Test
        @DisplayName("Should return 409 when cancelling a concluded atendimento")
        void shouldReturn409WhenAtendimentoIsConcluded() {
            Atendimento concluido = Atendimento.reconstituir(
                    AtendimentoId.novo(), AnimalId.of(UUID.randomUUID()), null,
                    StatusAtendimento.CONCLUIDO, LocalDateTime.now().minusHours(2),
                    LocalDateTime.now().minusHours(1), null,
                    null, null, List.of(), List.of(), List.of());
            atendimentoRepository.salvar(concluido);
            UUID id = concluido.getId().getValue();

            given().contentType("application/json").port(port)
                    .header("Authorization", "Bearer " + token)
                    .body(Map.of("justificativa", "Tentativa de cancelar atendimento concluído"))
                    .when().post("/api/v1/atendimentos/" + id + "/cancelamento")
                    .then().log().ifValidationFails(LogDetail.BODY)
                    .statusCode(409)
                    .contentType(ContentType.JSON)
                    .body("message", not(emptyOrNullString()));
        }
    }

    @Nested
    @DisplayName("POST /{id}/conclusao")
    class Conclusao {
        @Test
        @DisplayName("Should return 200 with CONCLUIDO status when atendimento is complete")
        void shouldReturn200WhenAtendimentoIsComplete() {
            Atendimento atendimento = EntityBuilder.createCompleteAtendimento();
            atendimentoRepository.salvar(atendimento);
            UUID id = atendimento.getId().getValue();

            given().contentType("application/json").port(port)
                    .header("Authorization", "Bearer " + token)
                    .when().post("/api/v1/atendimentos/" + id + "/conclusao")
                    .then().log().ifValidationFails(LogDetail.BODY)
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body("id", equalTo(id.toString()))
                    .body("status", equalTo("CONCLUIDO"))
                    .body("dataHoraConclusao", notNullValue());
        }

        @Test
        @DisplayName("Should return 422 when atendimento has no vital signs")
        void shouldReturn422WhenNoVitalSigns() {
            UUID id = givenAtendimentoEmAndamento().getId().getValue();

            given().contentType("application/json").port(port)
                    .header("Authorization", "Bearer " + token)
                    .when().post("/api/v1/atendimentos/" + id + "/conclusao")
                    .then().log().ifValidationFails(LogDetail.BODY)
                    .statusCode(422)
                    .contentType(ContentType.JSON)
                    .body("message", not(emptyOrNullString()));
        }

        @Test
        @DisplayName("Should return 422 when atendimento has no diagnosis")
        void shouldReturn422WhenNoDiagnosis() {
            Atendimento atendimento = givenAtendimentoEmAndamento();
            atendimento.registrarSinaisVitais(new SinaisVitais(10, 10, 10));
            atendimentoRepository.salvar(atendimento);
            UUID id = atendimento.getId().getValue();

            given().contentType("application/json").port(port)
                    .header("Authorization", "Bearer " + token)
                    .when().post("/api/v1/atendimentos/" + id + "/conclusao")
                    .then().log().ifValidationFails(LogDetail.BODY)
                    .statusCode(422)
                    .contentType(ContentType.JSON)
                    .body("message", not(emptyOrNullString()));
        }

        @Test
        @DisplayName("Should return 404 when atendimento does not exist")
        void shouldReturn404WhenAtendimentoDoesNotExist() {
            given().contentType("application/json").port(port)
                    .header("Authorization", "Bearer " + token)
                    .when().post("/api/v1/atendimentos/" + UUID.randomUUID() + "/conclusao")
                    .then().log().ifValidationFails(LogDetail.BODY)
                    .statusCode(404)
                    .contentType(ContentType.JSON)
                    .body("message", not(emptyOrNullString()));
        }

        @Test
        @DisplayName("Should return 409 when atendimento has an open prescription")
        void shouldReturn409WhenAtendimentoHasOpenPrescription() {
            Atendimento atendimento = EntityBuilder.createCompleteAtendimento();
            atendimento.emitirPrescricao(List.of(EntityBuilder.createRandomItemPrescricao()));
            atendimentoRepository.salvar(atendimento);
            UUID id = atendimento.getId().getValue();

            given().contentType("application/json").port(port)
                    .header("Authorization", "Bearer " + token)
                    .when().post("/api/v1/atendimentos/" + id + "/conclusao")
                    .then().log().ifValidationFails(LogDetail.BODY)
                    .statusCode(409)
                    .contentType(ContentType.JSON)
                    .body("message", not(emptyOrNullString()));
        }

        @Test
        @DisplayName("Should return 409 when concluding a cancelled atendimento")
        void shouldReturn409WhenAtendimentoIsCancelled() {
            Atendimento atendimento = EntityBuilder.createCompleteAtendimento();
            atendimento.cancelar("Tutor desistiu do atendimento");
            atendimentoRepository.salvar(atendimento);
            UUID id = atendimento.getId().getValue();

            given().contentType("application/json").port(port)
                    .header("Authorization", "Bearer " + token)
                    .when().post("/api/v1/atendimentos/" + id + "/conclusao")
                    .then().log().ifValidationFails(LogDetail.BODY)
                    .statusCode(409)
                    .contentType(ContentType.JSON)
                    .body("message", not(emptyOrNullString()));
        }
    }

    @Nested
    @DisplayName("POST /{id}/reabertura")
    class Reabertura {

        @Test
        @DisplayName("Should return 200 with EM_ANDAMENTO status when reopening within the window")
        void shouldReturn200WhenReopeningWithinWindow() {
            Atendimento atendimento = EntityBuilder.createCompleteAtendimento();
            atendimento.concluir();
            atendimentoRepository.salvar(atendimento);
            UUID id = atendimento.getId().getValue();

            given().contentType("application/json").port(port)
                    .header("Authorization", "Bearer " + token)
                    .when().post("/api/v1/atendimentos/" + id + "/reabertura")
                    .then().log().ifValidationFails(LogDetail.BODY)
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body("id", equalTo(id.toString()))
                    .body("status", equalTo("EM_ANDAMENTO"));
        }

        @Test
        @DisplayName("Should return 404 when atendimento does not exist")
        void shouldReturn400WhenAtendimentoDoesNotExist() {
            given().contentType("application/json").port(port)
                    .header("Authorization", "Bearer " + token)
                    .when().post("/api/v1/atendimentos/" + UUID.randomUUID() + "/reabertura")
                    .then().log().ifValidationFails(LogDetail.BODY)
                    .statusCode(404)
                    .contentType(ContentType.JSON)
                    .body("message", not(emptyOrNullString()));
        }

        @Test
        @DisplayName("Should return 409 when atendimento is not concluded")
        void shouldReturn409WhenAtendimentoIsNotConcluded() {
            Atendimento atendimento = EntityBuilder.createCompleteAtendimento();
            atendimentoRepository.salvar(atendimento);
            UUID id = atendimento.getId().getValue();

            given().contentType("application/json").port(port)
                    .header("Authorization", "Bearer " + token)
                    .when().post("/api/v1/atendimentos/" + id + "/reabertura")
                    .then().log().ifValidationFails(LogDetail.BODY)
                    .statusCode(409)
                    .contentType(ContentType.JSON)
                    .body("message", not(emptyOrNullString()));
        }

        @Test
        @DisplayName("Should return 409 when atendimento is cancelled")
        void shouldReturn409WhenAtendimentoIsCanceled() {
            Atendimento atendimento = EntityBuilder.createCompleteAtendimento();
            atendimento.cancelar("Tutor desistiu do atendimento");
            atendimentoRepository.salvar(atendimento);
            UUID id = atendimento.getId().getValue();

            given().contentType("application/json").port(port)
                    .header("Authorization", "Bearer " + token)
                    .when().post("/api/v1/atendimentos/" + id + "/reabertura")
                    .then().log().ifValidationFails(LogDetail.BODY)
                    .statusCode(409)
                    .contentType(ContentType.JSON)
                    .body("message", not(emptyOrNullString()));
        }

        @Test
        @DisplayName("Should return 422 when reopening after the 24h window")
        void shouldReturn422WhenWindowExpired() {
            Atendimento expirado = Atendimento.reconstituir(
                    AtendimentoId.novo(), AnimalId.of(UUID.randomUUID()), null,
                    StatusAtendimento.CONCLUIDO,
                    LocalDateTime.now().minusHours(26),
                    LocalDateTime.now().minusHours(25),
                    null, null, null, List.of(), List.of(), List.of());
            atendimentoRepository.salvar(expirado);
            UUID id = expirado.getId().getValue();

            given().contentType("application/json").port(port)
                    .header("Authorization", "Bearer " + token)
                    .when().post("/api/v1/atendimentos/" + id + "/reabertura")
                    .then().log().ifValidationFails(LogDetail.BODY)
                    .statusCode(422)
                    .contentType(ContentType.JSON)
                    .body("message", not(emptyOrNullString()));
        }
    }

    @Nested
    @DisplayName("GET /{id}")
    class Buscar {

        @Test
        @DisplayName("Should return 200 with the atendimento data")
        void shouldReturn200WithAtendimentoData() {
            Atendimento atendimento = EntityBuilder.createCompleteAtendimento();
            atendimentoRepository.salvar(atendimento);
            UUID id = atendimento.getId().getValue();

            given().contentType("application/json").port(port)
                    .header("Authorization", "Bearer " + token)
                    .when().get("/api/v1/atendimentos/" + id)
                    .then().log().ifValidationFails(LogDetail.BODY)
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body("id", equalTo(id.toString()))
                    .body("animalId", equalTo(atendimento.getAnimalId().getValue().toString()))
                    .body("status", equalTo("EM_ANDAMENTO"))
                    .body("sinaisVitais", notNullValue())
                    .body("diagnosticos", hasSize(1))
                    .body("prescricoes", empty())
                    .body("exames", empty());
        }

        @Test
        @DisplayName("Should return 404 when atendimento does not exists")
        void shouldReturn404WhenAtendimentoDoesNotExists() {

            given().contentType("application/json").port(port)
                    .header("Authorization", "Bearer " + token)
                    .when().get("/api/v1/atendimentos/" + UUID.randomUUID())
                    .then().log().ifValidationFails(LogDetail.BODY)
                    .statusCode(404)
                    .contentType(ContentType.JSON)
                    .body("message", not(emptyOrNullString()));
        }
    }
}