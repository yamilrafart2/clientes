package com.fube.clientes.controladores;

import com.fube.clientes.excepciones.RecursoNoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * =============================================================================
 * MANEJADOR GLOBAL DE EXCEPCIONES: ManejadorGlobalExcepciones
 * [Unidad 1 y 2 - REST APIs & Resiliencia en Servicios Distribuidos]
 * =============================================================================
 *
 * CONTEXTO ARQUITECTÓNICO:
 * La anotación @RestControllerAdvice intercepta todas las excepciones lanzadas
 * por los @RestController de la aplicación mediante el patrón Aspect-Oriented
 * Programming (AOP).
 *
 * BENEFICIOS DE DISEÑO:
 * 1. Desacoplamiento: Evita bloques try-catch repetitivos en los controladores.
 * 2. Estandarización de Errores: Garantiza que el cliente HTTP reciba un JSON
 *    con una estructura homogénea (Error Response Payload) frente a cualquier falla.
 * 3. Mapeo Semántico HTTP: Traduce excepciones Java de la capa de dominio a
 *    códigos de estado del protocolo HTTP (e.g., 404 Not Found, 400 Bad Request).
 */
@RestControllerAdvice
public class ManejadorGlobalExcepciones {

    /**
     * Captura la excepción personalizada 'RecursoNoEncontradoException' cuando
     * se busca una entidad por ID en la BD y no existe.
     *
     * @param ex Instancia de la excepción capturada con el mensaje de error de dominio.
     * @return ResponseEntity conteniendo el mapa de error estandarizado y HTTP Status 404.
     */
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> manejarRecursoNoEncontrado(RecursoNoEncontradoException ex) {
        // LinkedHashMap preserva el orden de inserción de las claves al serializarse a JSON
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.NOT_FOUND.value()); // Status 404
        body.put("error", HttpStatus.NOT_FOUND.getReasonPhrase()); // "Not Found"
        body.put("mensaje", ex.getMessage()); // Mensaje dinámico de la excepción

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }
}