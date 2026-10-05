package com.fube.clientes.servicios;

import com.fube.clientes.dto.ClienteDTO;
import com.fube.clientes.modelos.Cliente;

import java.util.List;

// =============================================================================
// PATRÓN SERVICE INTERFACE [Unidad 1]
// Define el contrato de las operaciones de negocio del módulo de Clientes.
// Permite desacoplar el "qué se hace" (contrato) del "cómo se hace" (implementación),
// facilitando el principio de Inversión de Dependencias (DIP de SOLID) y el mockeo
// de dependencias durante las pruebas unitarias con Mockito (Unidad 4).
// =============================================================================
public interface ClienteServicio {

    ClienteDTO crear(Cliente cliente);
    ClienteDTO buscarPorId(Long id);
    List<ClienteDTO> buscarTodos();
    ClienteDTO actualizar(Cliente cliente);
    void eliminar(Long id);
}