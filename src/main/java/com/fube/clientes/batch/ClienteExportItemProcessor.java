package com.fube.clientes.batch;

import com.fube.clientes.dto.ClienteExportCsvDTO;
import com.fube.clientes.modelos.Cliente;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

/**
 * Componente ItemProcessor para la transformacion y formateo de los clientes a exportar.
 * Mapea desde la entidad 'Cliente' (persistida en PostgreSQL) hacia 'ClienteExportCsvDTO'.
 *
 * Implementa ItemProcessor<I, O> donde:
 *  - I (Input): Cliente (Entidad de dominio leida desde PostgreSQL).
 *  - O (Output): ClienteExportCsvDTO (DTO formateado listo para ser escrito en el CSV).
 */
@Slf4j
@Component // Registra la clase como un Bean administrado por el contenedor de IoC de Spring.
public class ClienteExportItemProcessor implements ItemProcessor<Cliente, ClienteExportCsvDTO> {

    /**
     * Transformación de cada cliente leido de la base de datos.
     *
     * @param cliente Entidad de dominio leida de PostgreSQL.
     * @return DTO de exportación con valores formateados (reemplazando nulls con "-").
     */
    @Override
    public ClienteExportCsvDTO process(Cliente cliente) throws Exception {

        // Evaluacion y asignacion por defecto "-" para telefono si viene null o vacio.
        String telefonoFormateado = (cliente.getTelefono() != null && !cliente.getTelefono().isBlank())
                ? cliente.getTelefono().trim()
                : "-";

        // Evaluacion y asignacion por defecto "-" para direccion si viene null o vacia.
        String direccionFormateada = (cliente.getDireccion() != null && !cliente.getDireccion().isBlank())
                ? cliente.getDireccion().trim()
                : "-";

        // Construccion del DTO formateado para la exportacion CSV
        return ClienteExportCsvDTO.builder()
                .id(cliente.getId())
                .nombre(cliente.getNombre())
                .apellido(cliente.getApellido())
                .email(cliente.getEmail())
                .telefono(telefonoFormateado)
                .direccion(direccionFormateada)
                .build();
    }
}