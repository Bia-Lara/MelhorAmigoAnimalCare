package br.ifsp.demo.controller.abertura;

import br.ifsp.demo.controller.BaseApiIntegrationTest;
import br.ifsp.demo.model.abertura.AnimalId;
import br.ifsp.demo.model.abertura.Atendimento;
import br.ifsp.demo.repository.AtendimentoRepository;
import br.ifsp.demo.security.user.User;
import io.restassured.filter.log.LogDetail;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

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
    }
}