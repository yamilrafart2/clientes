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
 * Runner de linea de comandos para la ejecucion explicita del Job de Importacion CSV (Ejercicio 1 - Guia 2).
 * Implementa CommandLineRunner, lo que garantiza que Spring Boot ejecutara el metodo run()
 * inmediatamente despues de que el ApplicationContext se haya cargado completamente.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ClienteCsvJobRunner implements CommandLineRunner {

    // Componente de Spring Batch encargado de lanzar la ejecucion de Jobs.
    private final JobLauncher jobLauncher;

    // Inyectamos especificamente el Bean de nuestro Job de CSV utilizando @Qualifier por el nombre del Bean.
    @Qualifier("importClienteCsvJob")
    private final Job importClienteCsvJob;

    @Override
    public void run(String... args) throws Exception {
        log.info(">>> Iniciando la ejecucion manual del Job: importClienteCsvJob <<<");

        // Creamos parametros unicos agregando un timestamp para permitir re-ejecuciones.
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        // Lanzamos el Job a traves del JobLauncher
        jobLauncher.run(importClienteCsvJob, jobParameters);
    }
}