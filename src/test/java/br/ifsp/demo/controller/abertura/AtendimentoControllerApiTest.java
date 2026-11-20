package br.ifsp.demo.controller.abertura;

import br.ifsp.demo.controller.BaseApiIntegrationTest;
import br.ifsp.demo.security.user.User;
import io.restassured.filter.log.LogDetail;
import org.junit.jupiter.api.*;

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
    }
}