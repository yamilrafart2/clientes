package com.fube.clientes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * =============================================================================
 * CAPA DE CONFIGURACIÓN DE INFRAESTRUCTURA: AppConfig
 * [Unidad 1 y 2 - Inversión de Control & Beans de Spring]
 * =============================================================================
 *
 * CONTEXTO ARQUITECTÓNICO:
 * Declara los componentes e infraestructuras de soporte que serán administrados
 * por el contenedor de Inyección de Dependencias (IoC) de Spring Boot.
 */
@Configuration
public class AppConfig {

    /**
     * Instancia el Bean singleton de RestTemplate para la realización de
     * peticiones HTTP sincrónicas hacia servicios REST externos.
     *
     * @return Instancia administrada de RestTemplate.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}