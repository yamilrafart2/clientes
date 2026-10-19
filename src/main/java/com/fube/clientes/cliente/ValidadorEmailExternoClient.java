package com.fube.clientes.cliente;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

/**
 * =============================================================================
 * CLIENTE HTTP DE INFRAESTRUCTURA: ValidadorEmailExternoClient
 * [Unidad 2 y 4 - Integración y Simulación de Servicios Externos]
 * =============================================================================
 *
 * CONTEXTO ARQUITECTÓNICO:
 * Encapsula la comunicación sincrónica HTTP hacia un proveedor externo de
 * validación de direcciones de correo electrónico.
 *
 * PRINCIPIOS DE DISEÑO:
 * - Inversión de Dependencias (DIP): La URL base se inyecta dinámicamente mediante
 *   la propiedad '${app.clientes.validador-email.url}', permitiendo desacoplar el
 *   entorno de producción de las pruebas de integración con MockServer.
 * - Resiliencia (Fail-Closed): Protege la aplicación ante caídas, latencias o
 *   errores 5xx del proveedor externo, garantizando una degradación controlada del sistema.
 */
@Component
public class ValidadorEmailExternoClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    /**
     * Inyección de dependencias por constructor.
     *
     * @param restTemplate Cliente sincrónico HTTP administrado por Spring IoC.
     * @param baseUrl URL base inyectada dinámicamente con valor por defecto para fallback local.
     */
    public ValidadorEmailExternoClient(
            RestTemplate restTemplate,
            @Value("${app.clientes.validador-email.url:http://localhost:8081}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    /**
     * Consulta el servicio externo enviando el correo como Query Parameter (?email=...).
     *
     * ESTRATEGIA DE RESILIENCIA Y MANEJO DE ERRORES:
     * - Fail-Closed (Seguridad/Consistencia): Ante cualquier falla de red (timeout)
     *   o respuesta de error del servidor (HTTP 5xx/4xx), se asume que el email NO es válido (retorna false).
     *
     * @param email Dirección de correo a verificar en el proveedor externo.
     * @return true si el servicio responde 200 OK con el cuerpo JSON {"valido": true}, false en cualquier otro caso.
     */
    public boolean esValido(String email) {
        // Construcción inmutable y segura de la URI parametrizada
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/validar-email")
                .queryParam("email", email)
                .toUriString();

        try {
            // Petición GET mapeando la respuesta JSON a un Map de Java
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("valido")) {
                return Boolean.TRUE.equals(response.get("valido"));
            }
            return false;

        } catch (HttpStatusCodeException e) {
            // Captura errores de estado HTTP (4xx Client Error, 5xx Server Error)
            return false; // Fail-Closed
        } catch (ResourceAccessException e) {
            // Captura I/O exceptions, timeouts de lectura/conexión y cortes de socket
            return false; // Fail-Closed
        } catch (Exception e) {
            // Manejo defensivo de excepciones imprevistas en runtime
            return false;
        }
    }
}