package com.fube.clientes.excepciones;

/**
 * =============================================================================
 * EXCEPCIÓN DE DOMINIO: RecursoNoEncontradoException [Unidad 4 & Clean Code]
 * =============================================================================
 *
 * CONTEXTO DE ARQUITECTURA:
 * Excepción no chequeada (Unchecked Exception - hereda de RuntimeException) que
 * representa el fallo de negocio cuando se solicita un recurso inexistente por su ID.
 * Permite que los controladores o un GlobalExceptionHandler de Spring intercepten el error
 * y respondan de forma centralizada con un código HTTP 404 NOT FOUND.
 */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}