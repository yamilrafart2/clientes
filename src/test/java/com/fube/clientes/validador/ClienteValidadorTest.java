package com.fube.clientes.validador;

import com.fube.clientes.modelos.Cliente;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * =============================================================================
 * SUITE DE PRUEBAS UNITARIAS: ClienteValidadorTest [Unidad 4 - TDD / JUnit 5]
 * =============================================================================
 *
 * CONTEXTO METODOLÓGICO:
 * Esta suite fue construida siguiendo de forma estricta las fases de TDD:
 *  1. RED (🔴): Creacion del test que falla antes de implementar la regla.
 *  2. GREEN (🟢): Implementacion minima de produccion para pasar la prueba.
 *  3. REFACTOR (🔵): Limpieza de codigo utilizando Apache Commons Validator y StringUtils.
 *
 * CONCEPTOS DE JUNIT 5 EVALUADOS:
 *  - Pruebas parametrizadas (@ParameterizedTest) para evaluacion dinamica de colecciones.
 *  - Fuentes de datos sinteticas (@ValueSource, @NullAndEmptySource).
 *  - Agrupacion de aserciones atomicas (assertAll).
 */
@DisplayName("Pruebas TDD - ClienteValidador (Validación de Emails y Datos de Cliente)")
class ClienteValidadorTest {

    // =========================================================================
    // EJERCICIO 1: Validación de Correo Electrónico (Email)
    // =========================================================================

    @Test
    @DisplayName("Debería aceptar un email sintético y correcto como 'juan@mail.com'")
    void deberiaAceptarEmailValido() {
        // ARRANGE & ACT
        boolean resultado = ClienteValidador.esEmailValido("juan@mail.com");

        // ASSERT
        assertTrue(resultado, "El email 'juan@mail.com' debe ser considerado valido");
    }

    /**
     * PRUEBA PARAMETRIZADA (Edge Cases Sintácticos):
     * Inyecta iterativamente cada String de @ValueSource en el argumento 'emailInvalido'.
     * Permite probar múltiples escenarios de falla sin duplicar código de test.
     */
    @ParameterizedTest
    @ValueSource(strings = {"juanmail.com", "juan@", "@mail.com", "juan@.com", "juan@mail."})
    @DisplayName("Debería rechazar emails con errores de sintaxis o sin dominio correcto")
    void deberiaRechazarEmailsConSintaxisInvalida(String emailInvalido) {
        assertFalse(ClienteValidador.esEmailValido(emailInvalido),
                "El email '" + emailInvalido + "' debería ser rechazado por sintaxis invalida");
    }

    /**
     * PRUEBA PARAMETRIZADA (Valores Nulos y Vacíos):
     * @NullAndEmptySource provee automáticamente: null, "" (cadena vacia) y "   " (espacios).
     */
    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Debería rechazar entradas nulas, vacías o compuestas solo por espacios")
    void deberiaRechazarEmailsNulosOVacios(String emailNuloOVacio) {
        assertFalse(ClienteValidador.esEmailValido(emailNuloOVacio),
                "Las referencias nulas o vacias deben retornar false");
    }

    // =========================================================================
    // EJERCICIO 2: Validación de Datos Completos (Cliente)
    // =========================================================================

    @Test
    @DisplayName("Debería responder true si el cliente tiene teléfono y dirección válidos")
    void deberiaAceptarClienteConDatosCompletos() {
        // ARRANGE: Instanciación de objeto de prueba mediante patrón Builder
        Cliente clienteCompleto = Cliente.builder()
                .id(1L)
                .nombre("Laura")
                .apellido("Gomez")
                .email("laura.gomez@email.com")
                .telefono("3424112233")
                .direccion("Av. Freyre 2000")
                .build();

        // ACT: Invocación a la regla de negocio
        boolean resultado = ClienteValidador.tieneDatosCompletos(clienteCompleto);

        // ASSERT
        assertTrue(resultado, "Un cliente con teléfono y dirección no nulos debe responder true");
    }

    @Test
    @DisplayName("Debería rechazar clientes que no posean teléfono o dirección cargados")
    void deberiaRechazarClienteIncompleto() {
        // GIVEN: Preparación de escenarios incompletos
        Cliente sinTelefono = Cliente.builder()
                .nombre("Pedro")
                .apellido("Rios")
                .telefono(null)
                .direccion("San Martín 1500")
                .build();

        Cliente direccionVacia = Cliente.builder()
                .nombre("Ana")
                .apellido("Perez")
                .telefono("3424998877")
                .direccion("   ")
                .build();

        // THEN: Evaluamos todos los casos de falla en un único bloque atómico
        assertAll("Verificación de rechazo por datos incompletos",
                () -> assertFalse(ClienteValidador.tieneDatosCompletos(null), "Un cliente null debe retornar false"),
                () -> assertFalse(ClienteValidador.tieneDatosCompletos(sinTelefono), "Cliente sin teléfono debe retornar false"),
                () -> assertFalse(ClienteValidador.tieneDatosCompletos(direccionVacia), "Cliente con dirección vacía debe retornar false")
        );
    }
}