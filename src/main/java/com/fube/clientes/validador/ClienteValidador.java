package com.fube.clientes.validador;

import com.fube.clientes.modelos.Cliente;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

/**
 * =============================================================================
 * COMPONENTE DE NEGOCIO: ClienteValidador [Unidad 4 - TDD / Clean Code]
 * =============================================================================
 *
 * CONTEXTO DE ARQUITECTURA:
 * Clase utilitaria sin estado (stateless) responsable de concentrar las reglas
 * de validacion sintactica y de dominio sobre la entidad Cliente.
 *
 * PATRONES Y BUENAS PRÁCTICAS (Robert C. Martin - Clean Code):
 *  1. Constructor privado: Evita la instanciacion innecesaria mediante 'new'.
 *  2. Uso de librerias probadas (Apache Commons): Evita expresiones regulares
 *     ad-hoc propensas a fallos de seguridad o casos bordes no contemplados.
 */
public class ClienteValidador {

    /**
     * Constructor privado para prevenir la instanciacion de la clase utilitaria.
     */
    private ClienteValidador() {
        throw new UnsupportedOperationException("Clase utilitaria - No instanciable");
    }

    /**
     * Valida si una cadena de texto posee un formato de email valido (Bloque B - Ejercicio 1).
     *
     * @param email Cadena de texto a evaluar.
     * @return 'true' si cumple la sintaxis de correo electronico; 'false' si es null,
     *         cadena vacia, de solo espacios o con sintaxis invalida.
     */
    public static boolean esEmailValido(String email) {
        // StringUtils.isBlank evalua simultaneamente: null, "" y "   " (whitespaces)
        if (StringUtils.isBlank(email)) {
            return false;
        }
        // Delegacion en el singleton thread-safe de Apache Commons Validator
        return EmailValidator.getInstance().isValid(email.trim());
    }

    /**
     * Valida si la entidad Cliente posee sus atributos de contacto requeridos (Bloque B - Ejercicio 2).
     *
     * REGLA DE NEGOCIO: Un cliente se considera 'completo' unicamente si posee
     * telefono Y direccion cargados con texto valido (no nulos ni vacios).
     *
     * @param cliente Instancia de la entidad JPA a validar.
     * @return 'true' si 'telefono' y 'direccion' contienen datos validos; 'false' en caso contrario.
     */
    public static boolean tieneDatosCompletos(Cliente cliente) {
        if (cliente == null) {
            return false; // Fail-fast defensivo contra referencias nulas
        }

        // StringUtils.isNotBlank garantiza que el String no sea null, no este vacio
        // y contenga al menos un caracter visible (no solo espacios).
        boolean tieneTelefono = StringUtils.isNotBlank(cliente.getTelefono());
        boolean tieneDireccion = StringUtils.isNotBlank(cliente.getDireccion());

        return tieneTelefono && tieneDireccion;
    }
}