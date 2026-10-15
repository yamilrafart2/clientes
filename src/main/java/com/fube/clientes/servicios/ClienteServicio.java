package com.fube.clientes.servicios;

import com.fube.clientes.dto.ClienteDTO;
import java.util.List;

/**
 * =============================================================================
 * PATRÓN SERVICE INTERFACE [Unidad 1 - SOLID / Dependency Inversion Principle]
 * =============================================================================
 *
 * CONTEXTO DE ARQUITECTURA:
 * Define el contrato público de operaciones de negocio para el dominio de Clientes.
 * Aplica el principio de Inversión de Dependencias (DIP): la capa de presentación (Controller)
 * depende de una abstracción (Interface) y no de una implementación concreta (ServiceImpl).
 * Esto facilita la sustitución de componentes y permite el uso de Mocks durante las pruebas.
 */
public interface ClienteServicio {

    ClienteDTO crear(ClienteDTO dto);

    ClienteDTO buscarPorId(Long id);

    List<ClienteDTO> buscarTodos();

    ClienteDTO actualizar(ClienteDTO dto);

    void eliminar(Long id);
}