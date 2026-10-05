package com.fube.clientes.mapper;

import com.fube.clientes.dto.ClienteDTO;
import com.fube.clientes.modelos.Cliente;

// =============================================================================
// PATRÓN MAPPER / DATA MAPPER [Unidad 1]
// Clase de utilidad estática encargada de transformar objetos de Dominio (@Entity)
// a Objetos de Transferencia de Datos (DTO) y viceversa.
// Permite mantener el desacoplamiento entre la capa de persistencia (PostgreSQL)
// y la capa de presentación/API REST.
// =============================================================================
public class ClienteMapper {

    // =========================================================================
    // MAPEO: Entity -> DTO
    // Toma la entidad de base de datos cargada por JPA y construye el DTO
    // que se enviará serializado en formato JSON hacia el cliente HTTP.
    // Utiliza el patrón Builder generado por Lombok en ClienteDTO.
    // =========================================================================
    public static ClienteDTO toDTO(Cliente cliente) {
        if (cliente == null) {
            return null; // Buena práctica: evita NullPointerException al mapear.
        }

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
    // Convierte el DTO que llega en el body de la petición HTTP (POST/PUT)
    // a una entidad manipulable por JPA para ser guardada en la BD.
    // =========================================================================
    public static Cliente toEntity(ClienteDTO dto) {
        if (dto == null) {
            return null; // Buena práctica: evita NullPointerException.
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