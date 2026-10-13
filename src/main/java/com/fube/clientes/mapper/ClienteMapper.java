package com.fube.clientes.mapper;

import com.fube.clientes.dto.ClienteDTO;
import com.fube.clientes.modelos.Cliente;
import java.util.Objects; // Importante para la validacion estricta de referencias

// =============================================================================
// PATRÓN MAPPER / DATA MAPPER [Unidad 1]
// Clase de utilidad estática encargada de transformar objetos de Dominio (@Entity)
// a Objetos de Transferencia de Datos (DTO) y viceversa.
// =============================================================================
public class ClienteMapper {

    // =========================================================================
    // MAPEO: Entity -> DTO
    // Valida explícitamente que la entidad no sea nula. Si recibe null, lanza
    // NullPointerException de forma inmediata (fail-fast).
    // =========================================================================
    public static ClienteDTO toDTO(Cliente cliente) {
        // Valida el argumento: si cliente es null, arroja NullPointerException con mensaje explícito.
        Objects.requireNonNull(cliente, "El objeto cliente a mapear no puede ser nulo");

        return ClienteDTO.builder()
                .id(cliente.getId())
                .nombre(cliente.getNombre())
                .apellido(cliente.getApellido())
                .email(cliente.getEmail())
                .telefono(cliente.getTelefono())
                .direccion(cliente.getDireccion())
                .build();
    }

    // =========================================================================
    // MAPEO: DTO -> Entity
    // =========================================================================
    public static Cliente toEntity(ClienteDTO dto) {
        if (dto == null) {
            return null; // Mantiene el enfoque defensivo opcional en toEntity
        }

        return Cliente.builder()
                .id(dto.getId())
                .nombre(dto.getNombre())
                .apellido(dto.getApellido())
                .email(dto.getEmail())
                .telefono(dto.getTelefono())
                .direccion(dto.getDireccion())
                .build();
    }
}