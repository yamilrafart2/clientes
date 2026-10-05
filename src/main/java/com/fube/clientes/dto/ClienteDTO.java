package com.fube.clientes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// =============================================================================
// PATRÓN DTO (Data Transfer Object) [Unidad 1]
// Objeto plano (POJO) diseñado exclusivamente para transportar datos a través
// de la red entre el cliente HTTP (Postman/Frontend) y la API REST.
// Evita exponer directamente la entidad de base de datos (@Entity).
// =============================================================================

// Genera getters, setters, equals, hashCode y toString en bytecode.
@Data

// Implementa el patrón creacional Builder para instanciar objetos con fluidez.
@Builder

// Constructor sin argumentos (necesario para la deserialización JSON con Jackson).
@NoArgsConstructor

// Constructor con todos los campos (requerido para que @Builder funcione correctamente).
@AllArgsConstructor
public class ClienteDTO {

    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String direccion;

}