package com.fube.clientes.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Runner de linea de comandos para la ejecucion explicita del Job de Exportacion CSV (Ejercicio 2 - Guia 2).
 * Implementa CommandLineRunner para disparar el proceso inmediatamente despues de inicializar el ApplicationContext.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ClienteExportJobRunner implements CommandLineRunner {

    // Componente de Spring Batch encargado de lanzar la ejecucion del Job
    private final JobLauncher jobLauncher;

    // Inyectamos especificamente el Bean 'exportClienteCsvJob' mediante @Qualifier
    @Qualifier("exportClienteCsvJob")
    private final Job exportClienteCsvJob;

    @Override
    public void run(String... args) throws Exception {
        log.info(">>> Iniciando la ejecucion manual del Job: exportClienteCsvJob <<<");

        // Parametros unicos agregando timestamp para permitir re-ejecuciones
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        // Lanzamos el Job mediante el JobLauncher
        jobLauncher.run(exportClienteCsvJob, jobParameters);
    }
}