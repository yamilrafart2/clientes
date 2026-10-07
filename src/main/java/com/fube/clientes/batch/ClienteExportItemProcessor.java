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
 * (Extra): Filtra clientes sin direccion.
 * Solo permite el paso de aquellos registros que posean una direccion valida y cargada.
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

        // 1. Verificacion de regla de negocio: ¿Tiene direccion cargada?
        if (cliente.getDireccion() == null || cliente.getDireccion().isBlank()) {
            // Logueo del descarte con nivel DEBUG o WARN para trazabilidad
            log.warn("Cliente excluido de exportacion (ID: {}, Email: {}): no tiene direccion cargada.",
                    cliente.getId(), cliente.getEmail());

            // Al retornar null, Spring Batch descarta el registro e incrementa stepExecution.getFilterCount()
            return null;
        }

        // 2. Evaluacion del telefono (asignacion de "-" si telefono es null)
        String telefonoFormateado = (cliente.getTelefono() != null && !cliente.getTelefono().isBlank())
                ? cliente.getTelefono().trim()
                : "-";

        // 3. Mapeo al DTO de exportacion
        return ClienteExportCsvDTO.builder()
                .id(cliente.getId())
                .nombre(cliente.getNombre().trim())
                .apellido(cliente.getApellido().trim())
                .email(cliente.getEmail().trim())
                .telefono(telefonoFormateado)
                .direccion(cliente.getDireccion().trim())
                .build();
    }
}