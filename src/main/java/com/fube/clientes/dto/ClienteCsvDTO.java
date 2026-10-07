package com.fube.clientes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) de representación para la lectura del CSV.
 * Mapea directamente las columnas del archivo 'clientes.csv' (nombre, apellido, email, telefono, direccion).
 *
 * Utiliza anotaciones de Project Lombok para eliminar código boilerplate.
 */
@Data                   // Genera automáticamente getters, setters, toString, equals y hashCode.
@NoArgsConstructor      // Genera el constructor sin argumentos (requerido por Spring Batch para la instanciación por reflexión).
@AllArgsConstructor     // Genera el constructor con todos los argumentos.
@Builder                // Permite la construcción mediante el patrón Builder.
public class ClienteCsvDTO {

    private String nombre;
    private String apellido;
    private String email;
    private String telefono;    // Puede venir vacío en el CSV (mapeado a null o String vacío).
    private String direccion;   // Puede venir vacío en el CSV (mapeado a null o String vacío).
}