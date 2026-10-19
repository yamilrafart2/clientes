package com.fube.clientes.repositorio;

import com.fube.clientes.modelos.Cliente;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * =============================================================================
 * TESTING DE PERSISTENCIA: ClienteRepositorioTest [Unidad 4 - Bloque D]
 * =============================================================================
 * Esta clase prueba de forma aislada la capa de acceso a datos (Spring Data JPA / Hibernate).
 *
 * CONCEPTOS CLAVE APLICADOS:
 * 1. @DataJpaTest: Anotación slice test de Spring Boot que deshabilita la configuración web
 *    y carga únicamente los componentes necesarios para JPA (Entidades, Repositorios, TestEntityManager).
 * 2. @AutoConfigureTestDatabase: Reemplaza dinámicamente el DataSource de producción (PostgreSQL)
 *    por una base de datos embebida en memoria (H2).
 * 3. @TestPropertySource: Inyecta propiedades exclusivas para el contexto del test:
 *    - spring.flyway.enabled=false: Desactiva las migraciones de Flyway para evitar ejecuciones
 *      de scripts de estructura PostgreSQL sobre la BD embebida.
 *    - spring.jpa.hibernate.ddl-auto=create-drop: Le indica a Hibernate que cree la estructura de
 *      tablas dinámicamente al iniciar el test y la destruya al finalizar.
 *    - spring.jpa.properties.hibernate.dialect: Configura el dialecto oficial de H2 para Hibernate 6+.
 *    - spring.datasource.url: Activa la URL con compatibilidad MODE=PostgreSQL en H2.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL"
})
@DisplayName("Pruebas de Persistencia - ClienteRepositorio (@DataJpaTest + H2)")
class ClienteRepositorioTest {

    @Autowired
    private ClienteRepositorio clienteRepositorio;

    // =========================================================================
    // EJERCICIO 1: Unicidad de Email (DataIntegrityViolationException)
    // =========================================================================
    /**
     * Verifica que la restricción de base de datos UNIQUE sobre la columna 'email'
     * funcione adecuadamente cuando se intenta persistir un registro duplicado.
     * Se utiliza saveAndFlush() para forzar el envío inmediato de la sentencia SQL
     * antes de que finalice la transacción del test.
     */
    @Test
    @DisplayName("Ej 1: Debería lanzar DataIntegrityViolationException al intentar guardar dos clientes con el mismo email")
    void deberiaLanzarExcepcionAlDuplicarEmail() {
        // GIVEN: Dos instancias de Cliente con el mismo correo electrónico
        Cliente cliente1 = Cliente.builder()
                .nombre("Juan")
                .apellido("Perez")
                .email("duplicado@mail.com")
                .telefono("3424111111")
                .direccion("Calle 1")
                .build();

        Cliente cliente2 = Cliente.builder()
                .nombre("Pedro")
                .apellido("Gomez")
                .email("duplicado@mail.com")
                .telefono("3424222222")
                .direccion("Calle 2")
                .build();

        // WHEN: Se guarda el primer cliente en la BD en memoria
        clienteRepositorio.saveAndFlush(cliente1);

        // THEN: El intento de guardar el segundo cliente debe disparar la excepción DataIntegrityViolationException
        assertThrows(
                DataIntegrityViolationException.class,
                () -> clienteRepositorio.saveAndFlush(cliente2),
                "Se esperaba DataIntegrityViolationException debido a la restricción UNIQUE en la columna email"
        );
    }

    // =========================================================================
    // EJERCICIO 2: Guardar y Buscar por ID (findById)
    // =========================================================================
    /**
     * Comprueba el flujo básico de inserción (C) y lectura (R) mediante la clave primaria de JPA.
     */
    @Test
    @DisplayName("Ej 2: Debería guardar un cliente y luego recuperarlo correctamente por ID")
    void deberiaGuardarYBuscarPorId() {
        // GIVEN: Un nuevo cliente a persistir
        Cliente nuevoCliente = Cliente.builder()
                .nombre("Maria")
                .apellido("Lopez")
                .email("maria.lopez@mail.com")
                .telefono("3424333333")
                .direccion("Av. San Martín 500")
                .build();

        // WHEN: Se guarda la entidad en el repositorio
        Cliente clienteGuardado = clienteRepositorio.save(nuevoCliente);

        // AND: Se busca por el identificador autogenerado
        Optional<Cliente> clienteEncontrado = clienteRepositorio.findById(clienteGuardado.getId());

        // THEN: Se valida la presencia de la entidad y la integridad de sus atributos
        assertTrue(clienteEncontrado.isPresent(), "El cliente debería ser encontrado en la BD H2");
        assertEquals("Maria", clienteEncontrado.get().getNombre(), "El nombre recuperado debe coincidir");
        assertEquals("maria.lopez@mail.com", clienteEncontrado.get().getEmail(), "El email recuperado debe coincidir");
    }

    // =========================================================================
    // EJERCICIO 3: Prueba de findByTelefonoIsNullOrDireccionIsNull (Programática)
    // =========================================================================
    /**
     * Valida el método derivado de Spring Data JPA que consulta los registros con
     * atributos incompletos (teléfono nulo O dirección nula).
     */
    @Test
    @DisplayName("Ej 3: Debería retornar únicamente los clientes incompletos (sin teléfono o sin dirección)")
    void deberiaBuscarClientesIncompletos() {
        // GIVEN: Tres clientes (uno completo, uno sin teléfono y uno sin dirección)
        Cliente completo = Cliente.builder()
                .nombre("Carlos")
                .apellido("Santi")
                .email("carlos@mail.com")
                .telefono("3424000000")
                .direccion("Calle 10")
                .build();

        Cliente sinTelefono = Cliente.builder()
                .nombre("Ana")
                .apellido("Rios")
                .email("ana@mail.com")
                .telefono(null)
                .direccion("Calle 20")
                .build();

        Cliente sinDireccion = Cliente.builder()
                .nombre("Luis")
                .apellido("Sosa")
                .email("luis@mail.com")
                .telefono("3424888888")
                .direccion(null)
                .build();

        clienteRepositorio.saveAllAndFlush(List.of(completo, sinTelefono, sinDireccion));

        // WHEN: Se invoca la consulta derivada de Spring Data JPA
        List<Cliente> incompletos = clienteRepositorio.findByTelefonoIsNullOrDireccionIsNull();

        // THEN: Se verifica que devuelva únicamente los dos registros incompletos
        assertEquals(2, incompletos.size(), "Deberían retornar exactamente 2 clientes incompletos");
        assertTrue(incompletos.stream().anyMatch(c -> "ana@mail.com".equals(c.getEmail())), "Debe incluir a Ana (sin teléfono)");
        assertTrue(incompletos.stream().anyMatch(c -> "luis@mail.com".equals(c.getEmail())), "Debe incluir a Luis (sin dirección)");
    }

    // =========================================================================
    // EJERCICIO 4: Prueba con Carga Inicial mediante Script SQL (@Sql)
    // =========================================================================
    /**
     * Prueba el mismo método de consulta que el Ejercicio 3, pero alimentando la BD
     * mediante la anotación @Sql de Spring Test, que ejecuta un archivo de datos .sql
     * previamente ubicado en el classpath de pruebas (src/test/resources).
     */
    @Test
    @Sql("/import-clientes-incompletos.sql")
    @DisplayName("Ej 4: Debería retornar los clientes incompletos cargados mediante el script SQL")
    void deberiaBuscarClientesIncompletosDesdeSql() {
        // WHEN: Se ejecuta la consulta derivada del repositorio
        List<Cliente> incompletos = clienteRepositorio.findByTelefonoIsNullOrDireccionIsNull();

        // THEN: Se evalúa que los registros precargados por el script SQL cumplan las condiciones
        assertEquals(2, incompletos.size(), "Deberían retornar exactamente 2 clientes incompletos desde el script SQL");
        assertTrue(incompletos.stream().anyMatch(c -> "ana@mail.com".equals(c.getEmail())), "Debe encontrar el registro de Ana cargado vía SQL");
        assertTrue(incompletos.stream().anyMatch(c -> "luis@mail.com".equals(c.getEmail())), "Debe encontrar el registro de Luis cargado vía SQL");
    }
}