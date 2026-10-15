package com.fube.clientes.servicios;

import com.fube.clientes.dto.ClienteDTO;
import com.fube.clientes.excepciones.RecursoNoEncontradoException;
import com.fube.clientes.modelos.Cliente;
import com.fube.clientes.repositorio.ClienteRepositorio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * =============================================================================
 * SUITE DE PRUEBAS UNITARIAS CON MOCKITO: ClienteServicioImplTest [Unidad 4]
 * =============================================================================
 *
 * CONTEXTO DE ARQUITECTURA Y PRUEBAS ISOLADAS:
 * Esta clase prueba de forma unitaria y aislada la capa de servicios (ClienteServicioImpl).
 * No levanta el contexto IoC de Spring Boot (@SpringBootTest) ni interactúa con la BD
 * PostgreSQL real, garantizando ejecuciones ultrarrápidas en milisegundos.
 *
 * CONCEPTOS DE MOCKITO Y JUNIT 5 EVALUADOS EN EL COLOQUIO:
 *  - @ExtendWith(MockitoExtension.class): Habilita e inicializa el ciclo de vida de Mockito.
 *  - @Mock: Crea un "doble de prueba" (mock) de ClienteRepositorio.
 *  - @InjectMocks: Instancia la clase real bajo prueba (ClienteServicioImpl) e inyecta
 *    automáticamente los mocks declarados por constructor.
 *  - Stubbing (when/thenReturn): Enseña al mock cómo responder ante invocaciones específicas.
 *  - Verification (verify / verifyNoMoreInteractions): Asegura que el servicio interactúe
 *    estrictamente con las dependencias esperadas sin llamadas redundantes a la BD.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - ClienteServicioImpl (Mockito & Isolation)")
class ClienteServicioImplTest {

    @Mock // Mock de la capa de persistencia (repositorio)
    private ClienteRepositorio repositorio;

    @InjectMocks // Instancia real del servicio con el mock inyectado
    private ClienteServicioImpl servicio;

    /**
     * TEST 1 (Ejercicio 1): Búsqueda por ID Inexistente.
     * Comprueba que si la BD devuelve Optional.empty(), el servicio responde
     * lanzando la excepción personalizada de dominio 'RecursoNoEncontradoException'.
     */
    @Test
    @DisplayName("Debería lanzar RecursoNoEncontradoException cuando el ID buscado no existe")
    void deberiaLanzarExcepcionCuandoClienteNoExiste() {
        // ---------------------------------------------------------------------
        // 1. ARRANGE / GIVEN: Entrenamiento del mock (Stubbing)
        // ---------------------------------------------------------------------
        Long idInexistente = 999L;
        // Cuando el repositorio consulte por 999L, devolverá Optional vacío simulando que no está en la BD
        when(repositorio.findById(idInexistente)).thenReturn(Optional.empty());

        // ---------------------------------------------------------------------
        // 2. ACT & 3. ASSERT / WHEN & THEN: Invocación y verificación de la excepción
        // ---------------------------------------------------------------------
        RecursoNoEncontradoException excepcion = assertThrows(
                RecursoNoEncontradoException.class,
                () -> servicio.buscarPorId(idInexistente),
                "Se esperaba RecursoNoEncontradoException al no hallar el ID en BD"
        );

        assertNotNull(excepcion.getMessage(), "El mensaje de la excepción no debe ser nulo");

        // ---------------------------------------------------------------------
        // 4. VERIFICATION / VERIFY: Verificación estricta de comportamiento
        // ---------------------------------------------------------------------
        // Verificamos que se llamó exactamente 1 vez a findById con el idInexistente
        verify(repositorio, times(1)).findById(idInexistente);
        // Garantizamos que no se ejecutaron métodos secundarios no autorizados en el repositorio
        verifyNoMoreInteractions(repositorio);
    }

    /**
     * TEST 2 (Ejercicio 2): Creación Exitosa de Cliente.
     * Simula la persistencia mediante repositorio.save() usando ArgumentMatchers
     * y verifica que el ClienteDTO resultante posea el ID asignado por la BD.
     */
    @Test
    @DisplayName("Debería crear un cliente exitosamente y retornar el DTO con el ID generado")
    void deberiaCrearClienteExitosamente() {
        // ---------------------------------------------------------------------
        // 1. ARRANGE / GIVEN: Preparación de DTO de entrada y entidad simulada
        // ---------------------------------------------------------------------
        ClienteDTO dtoEntrada = ClienteDTO.builder()
                .nombre("Gonzalo")
                .apellido("Perez")
                .email("gonzalo.perez@email.com")
                .telefono("3424112233")
                .direccion("San Jerónimo 3000")
                .build();

        // Entidad simulada devuelta por la BD tras asignar la clave primaria (ID 50L)
        Cliente clientePersistido = Cliente.builder()
                .id(50L)
                .nombre(dtoEntrada.getNombre())
                .apellido(dtoEntrada.getApellido())
                .email(dtoEntrada.getEmail())
                .telefono(dtoEntrada.getTelefono())
                .direccion(dtoEntrada.getDireccion())
                .build();

        // Entrenamiento de Mockito con Matchers: intercepta cualquier instancia de Cliente que se guarde
        when(repositorio.save(any(Cliente.class))).thenReturn(clientePersistido);

        // ---------------------------------------------------------------------
        // 2. ACT / WHEN: Ejecución de la lógica de negocio
        // ---------------------------------------------------------------------
        ClienteDTO dtoResultante = servicio.crear(dtoEntrada);

        // ---------------------------------------------------------------------
        // 3. ASSERT / THEN: Verificación de atributos del DTO mapeado
        // ---------------------------------------------------------------------
        assertNotNull(dtoResultante, "El DTO resultante no debe ser nulo");
        assertAll("Verificación de campos mapeados tras la persistencia",
                () -> assertEquals(50L, dtoResultante.getId(), "El ID retornado debe ser 50L"),
                () -> assertEquals(dtoEntrada.getNombre(), dtoResultante.getNombre()),
                () -> assertEquals(dtoEntrada.getApellido(), dtoResultante.getApellido()),
                () -> assertEquals(dtoEntrada.getEmail(), dtoResultante.getEmail())
        );

        // ---------------------------------------------------------------------
        // 4. VERIFICATION / VERIFY: Verificación de interacción y ausencia de efectos colaterales
        // ---------------------------------------------------------------------
        verify(repositorio, times(1)).save(any(Cliente.class));
        verifyNoMoreInteractions(repositorio);
    }
}