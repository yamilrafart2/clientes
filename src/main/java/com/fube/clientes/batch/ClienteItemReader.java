package com.fube.clientes.batch;

import com.fube.clientes.modelos.Cliente;
import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.stereotype.Component;

// =============================================================================
// LECTOR PAGINADO CON JPA (Unidad 3: Procesamiento Batch a Gran Escala)
// =============================================================================

// Inyecta el logger SLF4J mediante Lombok para trazabilidad.
@Slf4j

// Registra la clase como un Bean en el IoC Container de Spring Boot.
@Component
public class ClienteItemReader extends JpaPagingItemReader<Cliente> {

    // =========================================================================
    // CONSTRUCTOR Y CONFIGURACIÓN DEL READER PAGINADO
    // =========================================================================
    public ClienteItemReader(EntityManagerFactory entityManagerFactory) {
        // Asigna un nombre identificatorio a la instancia del Reader en el contexto.
        setName("clienteItemReader");

        // Inyecta el EntityManagerFactory de JPA para gestionar las conexiones a Postgres.
        setEntityManagerFactory(entityManagerFactory);

        // Consulta JPQL (Java Persistence Query Language):
        // Selecciona todos los clientes que tengan teléfono O dirección nula.
        setQueryString("SELECT c FROM Cliente c WHERE c.telefono IS NULL OR c.direccion IS NULL");

        // Tamaño de página: Lee de a 10 registros por cada consulta SQL (LIMIT/OFFSET en Postgres).
        setPageSize(10);
    }

    // =========================================================================
    // SOBRESCRITURA DEL MÉTODO READ()
    // Intercepta cada lectura individual para generar trazabilidad y monitoreo.
    // =========================================================================
    @Override
    public Cliente read() throws Exception {
        // Delega en la clase padre (JpaPagingItemReader) la lectura del siguiente registro.
        Cliente cliente = super.read();

        if (cliente != null) {
            log.info("Leyendo cliente id={} | telefono={} | direccion={}",
                    cliente.getId(), cliente.getTelefono(), cliente.getDireccion());
        } else {
            // Cuando la consulta JPQL no devuelve más filas, super.read() retorna null
            // notificando a Spring Batch que el Step debe finalizar la fase de lectura.
            log.info("Reader finalizado, no hay más clientes para procesar");
        }

        return cliente;
    }
}