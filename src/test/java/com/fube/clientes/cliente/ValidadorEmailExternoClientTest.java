package com.fube.clientes.cliente;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.MediaType;
import org.mockserver.model.Parameter;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

/**
 * =============================================================================
 * TESTING DE INTEGRACIÓN CON MOCKSERVER: ValidadorEmailExternoClientTest
 * [Unidad 4 - Bloque F]
 * =============================================================================
 *
 * PROPÓSITO:
 * Evalúa el cliente de infraestructura 'ValidadorEmailExternoClient' contra un
 * servidor HTTP real instanciado sobre un puerto local efímero mediante MockServer.
 *
 * DIFERENCIA CLAVE CON MOCKITO:
 * A diferencia de Mockito (que intercepta llamadas a métodos Java en la JVM),
 * MockServer ejercita la pila real de red (sockets, serialización JSON, headers HTTP,
 * timeouts y resolución de URIs).
 */
@DisplayName("Pruebas de Integración HTTP - ValidadorEmailExternoClient (MockServer)")
class ValidadorEmailExternoClientTest {

    private static ClientAndServer mockServer;
    private ValidadorEmailExternoClient validadorClient;

    /**
     * Inicia una instancia real de MockServer escuchando en un puerto dinámico (0)
     * antes de ejecutar la suite de pruebas.
     */
    @BeforeAll
    static void startMockServer() {
        mockServer = startClientAndServer(0);
    }

    /**
     * Apaga la instancia de MockServer al finalizar la totalidad de los tests
     * para liberar el socket de red.
     */
    @AfterAll
    static void stopMockServer() {
        if (mockServer != null) {
            mockServer.stop();
        }
    }

    /**
     * Reinicia stubs, expectativas y logs de peticiones antes de cada test,
     * garantizando el aislamiento total entre casos de prueba.
     */
    @BeforeEach
    void setUp() {
        mockServer.reset();

        // Vinculamos el cliente a la dirección IP y puerto dinámico generado por MockServer
        String baseUrl = "http://localhost:" + mockServer.getPort();
        validadorClient = new ValidadorEmailExternoClient(new RestTemplate(), baseUrl);
    }

    // =========================================================================
    // EJERCICIO 2: Stubbing 200 OK
    // =========================================================================
    @Test
    @DisplayName("Ej 2: Debería retornar true cuando el servicio externo responde 200 OK con {'valido': true}")
    void deberiaRetornarTrueCuandoServicioExternoRespondeValido() {
        // GIVEN: Configuración de stub/expectativa HTTP en MockServer
        mockServer.when(
                request()
                        .withMethod("GET")
                        .withPath("/validar-email")
                        .withQueryStringParameter("email", "juan@mail.com")
        ).respond(
                response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody("{\"valido\": true}")
        );

        // WHEN: Ejecución de la llamada síncrona
        boolean resultado = validadorClient.esValido("juan@mail.com");

        // THEN: Verificación de la deserialización de la respuesta
        assertTrue(resultado, "Debería interpretar correctamente la respuesta 'valido: true'");
    }

    // =========================================================================
    // EJERCICIO 3: Resiliencia ante Errores 500 y Timeouts (Fail-Closed)
    // =========================================================================
    @Test
    @DisplayName("Ej 3.1: Debería aplicar estrategia Fail-Closed (retornar false) cuando el servicio responde HTTP 500")
    void deberiaRetornarFalseAnteError500() {
        // GIVEN: Simulación de error grave de servidor (HTTP 500 Internal Server Error)
        mockServer.when(
                request()
                        .withMethod("GET")
                        .withPath("/validar-email")
        ).respond(
                response()
                        .withStatusCode(500)
                        .withBody("{\"error\": \"Internal Server Error\"}")
        );

        // WHEN
        boolean resultado = validadorClient.esValido("error@mail.com");

        // THEN: Captura de HttpStatusCodeException y degradación a false
        assertFalse(resultado, "Ante un error 500 del servicio externo, debe aplicar Fail-Closed y retornar false");
    }

    @Test
    @DisplayName("Ej 3.2: Debería aplicar estrategia Fail-Closed (retornar false) ante latencia/timeout de red")
    void deberiaRetornarFalseAnteTimeout() {
        // GIVEN: Simulación de latencia de red aplicando un delay de 3 segundos
        mockServer.when(
                request()
                        .withMethod("GET")
                        .withPath("/validar-email")
        ).respond(
                response()
                        .withStatusCode(200)
                        .withDelay(TimeUnit.SECONDS, 3)
        );

        // WHEN
        boolean resultado = validadorClient.esValido("delay@mail.com");

        // THEN: Captura del retraso/timeout y respuesta de salvaguarda
        assertFalse(resultado, "Ante una demora o timeout en la respuesta, debe retornar false por resiliencia");
    }

    // =========================================================================
    // EJERCICIO 4: Verificación de Contrato HTTP con MockServer
    // =========================================================================
    @Test
    @DisplayName("Ej 4: Debería verificar que la petición HTTP enviada cumpla el contrato de la API (Método, Path y Query Parameter)")
    void deberiaVerificarRequestEnviada() {
        // GIVEN: Stubbing básico para permitir la ejecución
        mockServer.when(
                request().withPath("/validar-email")
        ).respond(
                response().withStatusCode(200).withBody("{\"valido\": false}")
        );

        // WHEN: Envío de la petición
        validadorClient.esValido("pedro@mail.com");

        // THEN: Inspección estricta en el registro de MockServer del contrato HTTP enviado
        mockServer.verify(
                request()
                        .withMethod("GET")
                        .withPath("/validar-email")
                        .withQueryStringParameter(Parameter.param("email", "pedro@mail.com"))
        );
    }
}