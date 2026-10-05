package com.fube.clientes.batch;

import com.fube.clientes.modelos.Cliente;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

// =============================================================================
// ANOTACIONES Y ROL EN LA ARQUITECTURA BATCH [Unidad 3]
// =============================================================================

// Inyecta el logger Slf4j de Lombok para trazabilidad en consola y archivos de log.
@Slf4j

// Registra la clase como un Bean en el IoC Container de Spring para ser inyectado en el Step.
@Component
public class ClienteItemProcessor implements ItemProcessor<Cliente, Cliente> {

    // Constantes de clase (Package-Private para facilitar pruebas unitarias con JUnit 5).
    static final String TELEFONO_DEFAULT = "0000-0000";
    static final String DIRECCION_DEFAULT = "Sin domicilio";

    // =========================================================================
    // LÓGICA DE PROCESAMIENTO / TRANSFORMACIÓN (ITEM PROCESSOR)
    // - Recibe: Un objeto Cliente extraído individualmente por el ItemReader.
    // - Retorna: El objeto Cliente modificado (o null si se quisiera filtrar).
    // =========================================================================
    @Override
    public Cliente process(Cliente cliente) {
        log.info("Procesando cliente id={}", cliente.getId());

        // Regla de Negocio 1: Sanitización/Completitud de teléfono
        if (cliente.getTelefono() == null) {
            cliente.setTelefono(TELEFONO_DEFAULT);
            log.info("  -> telefono nulo, asignando valor por defecto: '{}'", TELEFONO_DEFAULT);
        }

        // Regla de Negocio 2: Sanitización/Completitud de dirección
        if (cliente.getDireccion() == null) {
            cliente.setDireccion(DIRECCION_DEFAULT);
            log.info("  -> direccion nula, asignando valor por defecto: '{}'", DIRECCION_DEFAULT);
        }

        return cliente; // Retorna el elemento transformado hacia el ItemWriter
    }
}