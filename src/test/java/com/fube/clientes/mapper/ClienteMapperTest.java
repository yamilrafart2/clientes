package com.fube.clientes.mapper;

import com.fube.clientes.dto.ClienteDTO;
import com.fube.clientes.modelos.Cliente;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * =============================================================================
 * SUITE DE PRUEBAS UNITARIAS: ClienteMapperTest [Unidad 4 - Bloque A]
 * =============================================================================
 * Demuestra el ciclo de vida completo de JUnit 5:
 *  - @BeforeAll / @AfterAll (A nivel de Clase - Ejecucion unica)
 *  - @BeforeEach / @AfterEach (A nivel de Test - Ejecucion por cada metodo)
 */
@DisplayName("Pruebas Unitarias - ClienteMapper (Mapeo entre Entidad y DTO)")
class ClienteMapperTest {

    // Atributo de prueba (Fixture) reinicializado antes de cada test por @BeforeEach
    private Cliente clienteDeEjemplo;

    /**
     * INICIALIZACIÓN GLOBAL DE SUITE: Se ejecuta UNA SOLA VEZ antes de crear la
     * instancia de la clase de prueba y antes de cualquier test.
     * REQUISITO TÉCNICO: Debe ser obligatoriamente 'static'.
     */
    @BeforeAll
    static void beforeAll() {
        System.out.println("==================================================================");
        System.out.println("[LOG - @BeforeAll] === INICIANDO CLIENTE MAPPER TEST SUITE ===");
        System.out.println("==================================================================");
    }

    /**
     * CIERRE GLOBAL DE SUITE: Se ejecuta UNA SOLA VEZ al finalizar la totalidad
     * de los métodos de prueba de la clase.
     * REQUISITO TÉCNICO: Debe ser obligatoriamente 'static'.
     */
    @AfterAll
    static void afterAll() {
        System.out.println("==================================================================");
        System.out.println("[LOG - @AfterAll] === FINALIZADA CLIENTE MAPPER TEST SUITE ===");
        System.out.println("==================================================================");
    }

    /**
     * SETUP INDIVIDUAL: Se ejecuta antes de CADA método @Test.
     */
    @BeforeEach
    void setUp() {
        System.out.println("  [LOG - @BeforeEach] Inicializando objeto 'clienteDeEjemplo' para el test...");

        clienteDeEjemplo = Cliente.builder()
                .id(100L)
                .nombre("Esteban")
                .apellido("Quito")
                .email("esteban.quito@email.com")
                .telefono("3424001122")
                .direccion("Av. Facundo Zuviría 4500")
                .build();
    }

    /**
     * TEARDOWN INDIVIDUAL: Se ejecuta después de CADA método @Test.
     */
    @AfterEach
    void tearDown() {
        System.out.println("  [LOG - @AfterEach] Test finalizado.");
    }

    /**
     * TEST 1: Mapeo de Entidad -> DTO con fixture reutilizable.
     */
    @Test
    @DisplayName("Deberia mapear correctamente la entidad clienteDeEjemplo a ClienteDTO")
    void deberiaMapearClienteAClienteDTOCompleto() {
        System.out.println("    -> Ejecutando Test 1: Entidad a DTO");

        ClienteDTO dtoResultante = ClienteMapper.toDTO(clienteDeEjemplo);

        assertNotNull(dtoResultante, "El DTO resultante no deberia ser null");
        assertAll("Verificacion de mapeo con fixture de @BeforeEach",
                () -> assertEquals(clienteDeEjemplo.getId(), dtoResultante.getId()),
                () -> assertEquals(clienteDeEjemplo.getNombre(), dtoResultante.getNombre()),
                () -> assertEquals(clienteDeEjemplo.getApellido(), dtoResultante.getApellido()),
                () -> assertEquals(clienteDeEjemplo.getEmail(), dtoResultante.getEmail()),
                () -> assertEquals(clienteDeEjemplo.getTelefono(), dtoResultante.getTelefono()),
                () -> assertEquals(clienteDeEjemplo.getDireccion(), dtoResultante.getDireccion())
        );
    }

    /**
     * TEST 2: Mapeo completo DTO -> Entidad JPA.
     */
    @Test
    @DisplayName("Deberia mapear correctamente todos los campos de ClienteDTO a entidad Cliente")
    void deberiaMapearClienteDTOAEntityCompleto() {
        System.out.println("    -> Ejecutando Test 2: DTO a Entidad Completo");

        ClienteDTO dto = ClienteDTO.builder()
                .id(10L)
                .nombre("Maria")
                .apellido("Gomez")
                .email("maria.gomez@email.com")
                .telefono("3425998877")
                .direccion("Av. Aristobulo del Valle 5000")
                .build();

        Cliente clienteResultante = ClienteMapper.toEntity(dto);

        assertNotNull(clienteResultante);
        assertAll("Verificacion DTO -> Entidad",
                () -> assertEquals(dto.getId(), clienteResultante.getId()),
                () -> assertEquals(dto.getNombre(), clienteResultante.getNombre()),
                () -> assertEquals(dto.getApellido(), clienteResultante.getApellido()),
                () -> assertEquals(dto.getEmail(), clienteResultante.getEmail()),
                () -> assertEquals(dto.getTelefono(), clienteResultante.getTelefono()),
                () -> assertEquals(dto.getDireccion(), clienteResultante.getDireccion())
        );
    }

    /**
     * TEST 3: Respeto de valores nulos al mapear DTO -> Entidad.
     */
    @Test
    @DisplayName("Deberia preservar los campos nulos (telefono y direccion) al mapear DTO a Cliente")
    void deberiaPreservarCamposNulosAlMapearDTOAEntity() {
        System.out.println("    -> Ejecutando Test 3: Preservación de Nulos");

        ClienteDTO dtoConNulos = ClienteDTO.builder()
                .id(20L)
                .nombre("Carlos")
                .apellido("Lopez")
                .email("carlos.lopez@email.com")
                .telefono(null)
                .direccion(null)
                .build();

        Cliente clienteResultante = ClienteMapper.toEntity(dtoConNulos);

        assertNotNull(clienteResultante);
        assertAll("Verificacion de preservacion de nulos",
                () -> assertEquals(dtoConNulos.getId(), clienteResultante.getId()),
                () -> assertEquals(dtoConNulos.getNombre(), clienteResultante.getNombre()),
                () -> assertEquals(dtoConNulos.getApellido(), clienteResultante.getApellido()),
                () -> assertEquals(dtoConNulos.getEmail(), clienteResultante.getEmail()),
                () -> assertNull(clienteResultante.getTelefono()),
                () -> assertNull(clienteResultante.getDireccion())
        );
    }

    /**
     * TEST 4 (Ejercicio 5): Manejo de entradas nulas con assertThrows().
     * Verifica que al invocar ClienteMapper.toDTO(null) se lance formalmente
     * una excepcion NullPointerException.
     */
    @Test
    @DisplayName("Deberia lanzar NullPointerException al intentar mapear un cliente null a DTO con assertThrows")
    void deberiaLanzarNullPointerExceptionAlMapearClienteNull() {
        System.out.println("    -> Ejecutando Test 4: Verificacion de excepcion con assertThrows()");

        // ARRANGE & ACT & ASSERT
        // assertThrows verifica que la ejecucion de la lambda dispare la excepcion esperada.
        NullPointerException excepcionCapturada = assertThrows(
                NullPointerException.class,
                () -> ClienteMapper.toDTO(null), // Accion que dispara el fallo
                "Se esperaba NullPointerException al invocar toDTO(null)"
        );

        // Verificamos de forma opcional el mensaje de error de la excepcion
        assertEquals("El objeto cliente a mapear no puede ser nulo", excepcionCapturada.getMessage());
    }
}