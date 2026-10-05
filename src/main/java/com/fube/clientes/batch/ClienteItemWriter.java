package com.fube.clientes.batch;

import com.fube.clientes.modelos.Cliente;
import com.fube.clientes.repositorio.ClienteRepositorio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

// =============================================================================
// ANOTACIONES Y COMPONENTE DE ESCRITURA EN BATCH [Unidad 3]
// =============================================================================

// Logger SLF4J de Lombok para el registro estructurado de eventos.
@Slf4j

// Registra la clase como un Bean gestionado por el IoC Container de Spring Boot.
@Component
public class ClienteItemWriter implements ItemWriter<Cliente> {

    // Dependencia del repositorio de Spring Data JPA (Inyección por constructor).
    private final ClienteRepositorio repositorio;

    public ClienteItemWriter(ClienteRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    // =========================================================================
    // ESCRITURA BATCH POR CHUNKS (Persistencia Eficiente)
    // - Recibe: Un objeto de tipo 'Chunk<Cliente>' que contiene la lista de
    //   elementos procesados durante la iteración actual (ej. 10 registros).
    // =========================================================================
    @Override
    public void write(Chunk<? extends Cliente> chunk) {
        // Log de monitoreo: Tamaño del bloque a persistir
        log.info("Escribiendo chunk de {} cliente(s)", chunk.size());

        // Iteración descriptiva para auditoría/debug sobre cada cliente actualizado
        chunk.getItems().forEach(c ->
                log.info("  -> Guardando cliente id={} | telefono='{}' | direccion='{}'",
                        c.getId(), c.getTelefono(), c.getDireccion())
        );

        // PERSISTENCIA EN LOTE:
        // Ejecuta un batch insert/update en PostgreSQL a través de Spring Data JPA.
        // Se ejecuta dentro de la transacción gestionada por el PlatformTransactionManager del Step.
        repositorio.saveAll(chunk.getItems());

        log.info("Chunk guardado exitosamente");
    }
}