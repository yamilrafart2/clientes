package com.fube.clientes.batch;

import com.fube.clientes.dto.ClienteCsvDTO;
import com.fube.clientes.modelos.Cliente;
import com.fube.clientes.repositorio.ClienteRepositorio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

/**
 * Componente ItemProcessor para el procesamiento de cada fila del archivo CSV.
 * Implemementa ItemProcessor<I, O> donde:
 *  - I (Input): ClienteCsvDTO (Objeto leído desde el archivo CSV).
 *  - O (Output): Cliente (Entidad JPA lista para ser escrita en PostgreSQL).
 */
@Slf4j                           // Genera automáticamente el logger 'log' de SLF4J/Logback.
@Component                       // Registra esta clase como un Bean administrado por el contenedor de IoC de Spring.
@RequiredArgsConstructor         // Genera el constructor con argumentos requeridos para inyección por constructor (final fields).
public class ClienteCsvItemProcessor implements ItemProcessor<ClienteCsvDTO, Cliente> {

    // Inyección de dependencia del repositorio JPA para consultar duplicados en la BD.
    private final ClienteRepositorio clienteRepositorio;

    /**
     * Procesa un ítem individual leído por el FlatFileItemReader.
     *
     * @param item DTO con la información cruda del CSV.
     * @return Entidad Cliente a persistir, o null si el registro debe descartarse.
     */
    @Override
    public Cliente process(ClienteCsvDTO item) throws Exception {

        // 1. Verificación de existencia previa por email en la base de datos
        if (clienteRepositorio.existsByEmail(item.getEmail())) {

            // Requisito Ejercicio 1: Loguear línea indicando el email descartado y el motivo.
            log.warn("Cliente descartado: el email {} ya existe en la base de datos.", item.getEmail());

            // En Spring Batch, retornar null indica que el ítem debe ser descartado (no pasa al Writer).
            return null;
        }

        // 2. Mapeo y construcción de la entidad Cliente a partir del DTO
        // Nota: Si 'telefono' o 'direccion' vienen vacíos ("" o null), la entidad los guarda como null.
        String telefonoLimpio = (item.getTelefono() != null && !item.getTelefono().isBlank())
                ? item.getTelefono().trim()
                : null;

        String direccionLimpia = (item.getDireccion() != null && !item.getDireccion().isBlank())
                ? item.getDireccion().trim()
                : null;

        Cliente clienteNuevo = Cliente.builder()
                .nombre(item.getNombre().trim())
                .apellido(item.getApellido().trim())
                .email(item.getEmail().trim())
                .telefono(telefonoLimpio)
                .direccion(direccionLimpia)
                .build();

        return clienteNuevo;
    }
}