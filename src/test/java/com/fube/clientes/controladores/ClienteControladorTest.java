package com.fube.clientes.controladores;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fube.clientes.dto.ClienteDTO;
import com.fube.clientes.modelos.Cliente;
import com.fube.clientes.repositorio.ClienteRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * =============================================================================
 * TESTING DE INTEGRACIÓN END-TO-END: ClienteControladorTest [Unidad 4 - Bloque E]
 * =============================================================================
 *
 * PROPÓSITO:
 * Evalúa el comportamiento integrado de toda la pila tecnológica de Spring Boot
 * (Controller -> ExceptionHandler -> Service -> Repository -> Base de Datos H2).
 *
 * ANOTACIONES CLAVE DE TESTING:
 * - @SpringBootTest: Inicia el contenedor completo de Inyección de Dependencias (ApplicationContext).
 * - @AutoConfigureMockMvc: Configura e inyecta la herramienta MockMvc para simular
 *   peticiones y respuestas HTTP sin necesidad de desplegar un servidor Tomcat real.
 * - @TestPropertySource: Sobreescribe dinámicamente las propiedades de 'application.properties'
 *   para aislar el entorno de pruebas usando H2 en memoria.
 *
 * ESTRATEGIA DE FIX DE DRIVER (H2 vs Spring Batch):
 * Se establece "spring.datasource.driver-class-name=org.h2.Driver" para evitar que HikariCP
 * o la autoconfiguración de Spring Batch intenten usar el driver de PostgreSQL en la URL de H2.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.datasource.driver-class-name=org.h2.Driver", // Forzamos driver explícito de H2
        "spring.datasource.url=jdbc:h2:mem:testdb_e;DB_CLOSE_DELAY=-1;MODE=PostgreSQL"
})
@DisplayName("Pruebas de Integración - ClienteControlador (@SpringBootTest + MockMvc)")
class ClienteControladorTest {

    @Autowired
    private MockMvc mockMvc; // Simula cliente HTTP consumiendo los endpoints REST

    @Autowired
    private ClienteRepositorio clienteRepositorio; // Acceso directo a BD para verificar estado

    @Autowired
    private ObjectMapper objectMapper; // Serializador/Deserializador JSON de Jackson

    /**
     * Garantiza el aislamiento de datos entre ejecuciones limpiando la BD
     * antes de ejecutar cada método de prueba.
     */
    @BeforeEach
    void setUp() {
        clienteRepositorio.deleteAll();
    }

    // =========================================================================
    // EJERCICIO 1: Verificación de Carga del Contexto
    // =========================================================================
    @Test
    @DisplayName("Ej 1: Verificación de carga correcta del ApplicationContext de Spring")
    void contextLoads() {
        // Valida que la configuración del proyecto y todos los Beans se instancien sin fallas
    }

    // =========================================================================
    // EJERCICIO 2.1: GET /api/v1/clientes/{id} - Cliente Existente
    // =========================================================================
    @Test
    @DisplayName("Ej 2.1: GET /api/v1/clientes/{id} con cliente existente retorna HTTP 200 y el body esperado")
    void deberiaObtenerClientePorIdExistente() throws Exception {
        // GIVEN: Un registro previamente persistido en H2
        Cliente cliente = Cliente.builder()
                .nombre("Martin")
                .apellido("Palermo")
                .email("optimista@mail.com")
                .telefono("3424000111")
                .direccion("Av. Siempre Viva 123")
                .build();
        Cliente clienteGuardado = clienteRepositorio.save(cliente);

        // WHEN & THEN: Petición HTTP GET al recurso e inspección de campos JSON
        mockMvc.perform(get("/api/v1/clientes/{id}", clienteGuardado.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // HTTP 200 OK
                .andExpect(jsonPath("$.id", is(clienteGuardado.getId().intValue())))
                .andExpect(jsonPath("$.nombre", is("Martin")))
                .andExpect(jsonPath("$.email", is("optimista@mail.com")));
    }

    // =========================================================================
    // EJERCICIO 2.2: GET /api/v1/clientes/{id} - Cliente Inexistente
    // =========================================================================
    @Test
    @DisplayName("Ej 2.2: GET /api/v1/clientes/{id} con ID inexistente retorna HTTP 404 Not Found")
    void deberiaRetornar404AlBuscarIdInexistente() throws Exception {
        Long idInexistente = 9999L;

        // WHEN & THEN: Petición a ID no registrado. Interceptado por ManejadorGlobalExcepciones
        mockMvc.perform(get("/api/v1/clientes/{id}", idInexistente)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // HTTP 404 Not Found
    }

    // =========================================================================
    // EJERCICIO 2.3: POST /api/v1/clientes - Creación Exitosa
    // =========================================================================
    @Test
    @DisplayName("Ej 2.3: POST /api/v1/clientes con DTO válido retorna HTTP 201 CREATED y persiste en repositorio")
    void deberiaCrearClienteExitosamente() throws Exception {
        // GIVEN: Un DTO de entrada válido
        ClienteDTO nuevoClienteDTO = ClienteDTO.builder()
                .nombre("Román")
                .apellido("Riquelme")
                .email("10@mail.com")
                .telefono("3424101010")
                .direccion("Calle 10")
                .build();

        // WHEN: Envío del cuerpo JSON vía POST
        mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevoClienteDTO)))
                .andExpect(status().isCreated()) // HTTP 201 CREATED
                .andExpect(jsonPath("$.nombre", is("Román")))
                .andExpect(jsonPath("$.email", is("10@mail.com")));

        // THEN: Confirmación física de la inserción en el repositorio H2
        assertEquals(1, clienteRepositorio.count(), "Debería haber exactamente 1 registro en la BD");
        assertTrue(clienteRepositorio.findAll().stream().anyMatch(c -> "10@mail.com".equals(c.getEmail())));
    }

    // =========================================================================
    // EJERCICIO 3: Estructura de ErrorResponse en ManejadorGlobalExcepciones
    // =========================================================================
    @Test
    @DisplayName("Ej 3: GET /api/v1/clientes/{id} inexistente retorna JSON estructurado tipo ErrorResponse")
    void deberiaRetornarEstructuraErrorResponseEn404() throws Exception {
        Long idInexistente = 8888L;

        // WHEN & THEN: Verificación de las claves del payload de error unificado
        mockMvc.perform(get("/api/v1/clientes/{id}", idInexistente)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.mensaje").exists());
    }

    // =========================================================================
    // EJERCICIO EXTRA (JACOCO): DELETE /api/v1/clientes/{id} - Borrado Exitoso
    // =========================================================================
    @Test
    @DisplayName("JaCoCo Extra: DELETE /api/v1/clientes/{id} borra la entidad y retorna HTTP 204 No Content")
    void deberiaEliminarClienteExitosamente() throws Exception {
        // GIVEN: Un cliente previamente persistido en la base de datos de test (H2)
        Cliente cliente = Cliente.builder()
                .nombre("Ariel")
                .apellido("Ortega")
                .email("burrito@mail.com")
                .telefono("3424999888")
                .direccion("Calle 10")
                .build();
        Cliente clienteGuardado = clienteRepositorio.save(cliente);

        // WHEN: Se realiza la petición HTTP DELETE
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .delete("/api/v1/clientes/{id}", clienteGuardado.getId()))
                .andExpect(status().isNoContent()); // HTTP 204

        // THEN: Se valida la eliminación del registro en el repositorio JPA
        assertEquals(0, clienteRepositorio.count(), "El registro debe ser eliminado de la BD");
    }
}