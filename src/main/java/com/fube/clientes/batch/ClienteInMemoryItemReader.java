package com.fube.clientes.batch;

import com.fube.clientes.modelos.Cliente;
import com.fube.clientes.repositorio.ClienteRepositorio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.stereotype.Component;

import java.util.List;

// =============================================================================
// ANOTACIONES Y CONFIGURACIÓN BATCH [Unidad 3]
// =============================================================================

// Logger SLF4J de Lombok para trazabilidad.
@Slf4j

// Register esta clase como un Componente Bean administrado por Spring.
@Component
public class ClienteInMemoryItemReader extends ListItemReader<Cliente> {

    // =========================================================================
    // INYECCIÓN DE DEPENDENCIA Y CARGA INICIAL
    // Hereda de ListItemReader<T>, un ItemReader provisto por Spring Batch que
    // itera sobre una lista en memoria (java.util.List).
    // =========================================================================
    public ClienteInMemoryItemReader(ClienteRepositorio repositorio) {
        // Pasa a la clase padre (ListItemReader) la lista devuelta por el método estático aux.
        super(cargarClientes(repositorio));
    }

    // =========================================================================
    // LÓGICA DE EXTRACCIÓN (READ)
    // Consulta mediante Spring Data JPA todos los clientes con datos faltantes
    // (sin teléfono O sin dirección) para ser procesados/normalizados en el Job.
    // =========================================================================
    private static List<Cliente> cargarClientes(ClienteRepositorio repositorio) {
        List<Cliente> clientes = repositorio.findByTelefonoIsNullOrDireccionIsNull();
        log.info("Clientes cargados en memoria para procesar: {}", clientes.size());
        return clientes;
    }
}