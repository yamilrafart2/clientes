package com.fube.clientes.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

// =============================================================================
// EJECUTOR CONDICIONAL DE JOBS EN SPRING BOOT [Unidad 3]
// =============================================================================

// Logger SLF4J de Lombok para trazabilidad.
@Slf4j

// Registra esta clase como un Bean gestionado por Spring.
//@Component

// CONTROL DE EJECUCIÓN VÍA PROPIEDADES (Punto Clave):
// Este Bean SOLO se instanciará y ejecutará si en el 'application.properties'
// la propiedad 'spring.batch.job.enabled' está explícitamente en 'true'.
@ConditionalOnProperty(name = "spring.batch.job.enabled", havingValue = "true")
public class ClienteJobRunner implements ApplicationRunner {

    // Interfaces clave de Spring Batch e inyección de dependencias:
    private final JobLauncher jobLauncher; // Motor que dispara la ejecución del Job
    private final Job fillClientesJob;    // El Job configurado en ClienteBatchConfig

    public ClienteJobRunner(JobLauncher jobLauncher, Job fillClientesJob) {
        this.jobLauncher = jobLauncher;
        this.fillClientesJob = fillClientesJob;
    }

    // =========================================================================
    // MÉTODO RUN() DE ApplicationRunner
    // Spring Boot ejecuta automáticamente este método justo después de que el
    // ApplicationContext se inicia por completo.
    // =========================================================================
    @Override
    public void run(ApplicationArguments args) throws Exception {
        // CONSTRUCCIÓN DE JOB PARAMETERS:
        // Spring Batch requiere parámetros únicos para identificar cada ejecución (JobInstance).
        // Al agregar un timestamp incremental, garantizamos que cada arranque se trate
        // como una nueva instancia ejecutale del Job.
        JobParameters params = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        log.info("Lanzando job manualmente con timestamp={}", params.getLong("timestamp"));

        // DISPARO DEL JOB:
        // JobLauncher ejecuta el Job 'fillClientesJob' pasándole los parámetros.
        jobLauncher.run(fillClientesJob, params);
    }
}