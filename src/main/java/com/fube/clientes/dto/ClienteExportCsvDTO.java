package com.fube.clientes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) para la exportacion de clientes hacia el archivo CSV.
 * Representa la estructura final formateada de cada linea en 'clientes_exportados.csv'.
 */
@Data                   // Genera getters, setters, toString, equals y hashCode con Lombok.
@NoArgsConstructor      // Constructor sin argumentos necesario para instanciación.
@AllArgsConstructor     // Constructor completo.
@Builder                // Patrón Builder para construccion fluida.
public class ClienteExportCsvDTO {

    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;    // Si en la BD es null, debe contener "-".
    private String direccion;   // Si en la BD es null, debe contener "-".
}