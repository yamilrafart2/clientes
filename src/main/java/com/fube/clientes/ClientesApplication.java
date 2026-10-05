package com.fube.clientes; // Declaración del paquete base de la aplicación.

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

// =============================================================================
// ANOTACIÓN CENTRAL: @SpringBootApplication
// Esta es una meta-anotación que combina 3 anotaciones clave de Spring Boot:
// 1. @Configuration: Declara que esta clase define beans de Spring en el Application Context.
// 2. @EnableAutoConfiguration: Le indica a Spring Boot que configure automáticamente
//    los componentes (como JPA, Tomcat, Jackson) según las dependencias del build.gradle.
// 3. @ComponentScan: Escanea clases anotadas con @Component, @Service, @Repository,
//    @RestController en este paquete ('com.fube.clientes') y todos sus subpaquetes.
// =============================================================================
@SpringBootApplication
public class ClientesApplication {

    public static void main(String[] args) {
        // ---------------------------------------------------------------------
        // CONFIGURACIÓN DE ZONA HORARIA GLOBAL:
        // Fuerza a que la máquina virtual de Java (JVM) maneje las fechas y horas
        // en estándar UTC (Coordinated Universal Time).
        // ¡Buena práctica crítica en Backend! Previene problemas de desfasaje
        // cuando la aplicación corra en servidores con distintos husos horarios
        // o al guardar timestamps en PostgreSQL.
        // ---------------------------------------------------------------------
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        // ---------------------------------------------------------------------
        // ARRANQUE DE SPRING BOOT:
        // Crea el ApplicationContext (contenedor IoC / Inversión de Control),
        // levanta el servidor web Tomcat embebido en el puerto 8080 (u otro),
        // inicializa la conexión con Postgres y deja la API lista para recibir HTTP.
        // ---------------------------------------------------------------------
        SpringApplication.run(ClientesApplication.class, args);
    }
}