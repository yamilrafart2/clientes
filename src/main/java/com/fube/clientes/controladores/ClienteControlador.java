package com.fube.clientes.controladores;

import com.fube.clientes.dto.ClienteDTO;
import com.fube.clientes.mapper.ClienteMapper;
import com.fube.clientes.servicios.ClienteServicio;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// =============================================================================
// ANOTACIONES DE CAPA CONTROLADOR REST [Unidad 1]
// =============================================================================

// @RestController: Registra la clase como un Controller de Spring MVC y aplica
// automáticamente @ResponseBody en todos los métodos (convierte los retornos a JSON).
@RestController

// @RequestMapping: Establece el "Path Base" para todos los endpoints de este controlador.
// Sigue el estándar de nombrado REST en plural y minúsculas: /api/v1/clientes
@RequestMapping("/api/v1/clientes")
public class ClienteControlador {

    // Inyección de dependencias por constructor (Inmutabilidad con 'final').
    private final ClienteServicio servicio;

    public ClienteControlador(ClienteServicio servicio) {
        this.servicio = servicio;
    }

    // =========================================================================
    // 1. CREAR RECURSO (POST)
    // HTTP Status: 201 CREATED (Indica que el recurso se creó con éxito).
    // @RequestBody: Le indica a Jackson que deserialice el JSON del Request Body a ClienteDTO.
    // =========================================================================
    @PostMapping
    public ResponseEntity<ClienteDTO> crear(@RequestBody ClienteDTO clienteDTO) {
        ClienteDTO nuevoCliente = servicio.crear(ClienteMapper.toEntity(clienteDTO));
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCliente);
    }

    // =========================================================================
    // 2. BUSCAR POR ID (GET)
    // HTTP Status: 200 OK.
    // @PathVariable: Extrae la variable desde la URL (/api/v1/clientes/{id}).
    // =========================================================================
    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(servicio.buscarPorId(id));
    }

    // =========================================================================
    // 3. BUSCAR TODOS (GET)
    // HTTP Status: 200 OK.
    // =========================================================================
    @GetMapping
    public ResponseEntity<List<ClienteDTO>> buscarTodos() {
        return ResponseEntity.ok(servicio.buscarTodos());
    }

    // =========================================================================
    // 4. ACTUALIZAR RECURSO COMPLETO (PUT)
    // HTTP Status: 200 OK.
    // =========================================================================
    @PutMapping("/{id}")
    public ResponseEntity<ClienteDTO> actualizar(@PathVariable Long id, @RequestBody ClienteDTO clienteDTO) {
        return ResponseEntity.ok(servicio.actualizar(ClienteMapper.toEntity(clienteDTO)));
    }

    // =========================================================================
    // 5. ELIMINAR RECURSO (DELETE)
    // HTTP Status: 204 NO CONTENT (Indica éxito sin cuerpo de respuesta).
    // =========================================================================
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
    }
}