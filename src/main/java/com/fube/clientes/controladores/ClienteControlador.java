package com.fube.clientes.controladores;

import com.fube.clientes.dto.ClienteDTO;
import com.fube.clientes.servicios.ClienteServicio;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * =============================================================================
 * CAPA REST CONTROLLER: ClienteControlador [Unidad 1 - HTTP & REST APIs]
 * =============================================================================
 *
 * CONTEXTO DE ARQUITECTURA:
 * Expone los endpoints HTTP siguiendo los estándares de diseño RESTful:
 *  - Nombres de recursos en plural y minúsculas (/api/v1/clientes).
 *  - Uso correcto de Verbos HTTP (GET, POST, PUT, DELETE).
 *  - Mapeo preciso de Códigos de Estado HTTP (200 OK, 201 Created, 204 No Content).
 *  - Desacoplamiento total: Solo recibe y retorna DTOs, sin exponer entidades JPA (@Entity).
 */
@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteControlador {

    // Inyección de dependencias por constructor (Inmutabilidad con 'final')
    private final ClienteServicio servicio;

    public ClienteControlador(ClienteServicio servicio) {
        this.servicio = servicio;
    }

    /**
     * POST /api/v1/clientes
     * Crea un nuevo cliente en el sistema.
     * HTTP Status: 201 CREATED.
     */
    @PostMapping
    public ResponseEntity<ClienteDTO> crear(@RequestBody ClienteDTO clienteDTO) {
        // Delega la transformación y persistencia a la capa de servicio
        ClienteDTO nuevoCliente = servicio.crear(clienteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCliente);
    }

    /**
     * GET /api/v1/clientes/{id}
     * Obtiene los datos de un cliente específico por su ID.
     * HTTP Status: 200 OK.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(servicio.buscarPorId(id));
    }

    /**
     * GET /api/v1/clientes
     * Lista la totalidad de los clientes registrados.
     * HTTP Status: 200 OK.
     */
    @GetMapping
    public ResponseEntity<List<ClienteDTO>> buscarTodos() {
        return ResponseEntity.ok(servicio.buscarTodos());
    }

    /**
     * PUT /api/v1/clientes/{id}
     * Actualiza integralmente los datos de un cliente existente.
     * HTTP Status: 200 OK.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClienteDTO> actualizar(@PathVariable Long id, @RequestBody ClienteDTO clienteDTO) {
        // Garantizamos pasar el id del Path Variable al DTO si no venía asignado
        clienteDTO.setId(id);
        return ResponseEntity.ok(servicio.actualizar(clienteDTO));
    }

    /**
     * DELETE /api/v1/clientes/{id}
     * Elimina físicamente el recurso de la base de datos por su clave primaria.
     * HTTP Status: 204 NO CONTENT (Éxito sin cuerpo de respuesta JSON).
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
    }
}