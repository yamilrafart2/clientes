package com.fube.clientes.batch;

import com.fube.clientes.modelos.Cliente;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

// =============================================================================
// ANOTACIONES Y CONFIGURACIÓN BATCH [Unidad 3]
// =============================================================================

// @Slf4j: Anotación de Lombok que inyecta automáticamente un Logger de SLF4J (log).
@Slf4j

// @Configuration: Declara la clase como una fuente de definiciones de Beans para el IoC Container.
@Configuration
public class ClienteBatchConfig {

    // =========================================================================
    // 1. DEFINICIÓN DEL STEP (Fase de Procesamiento Orientado a Chunks)
    // =========================================================================
    @Bean
    public Step fillClientesStep(JobRepository jobRepository,
                                 PlatformTransactionManager transactionManager,
                                 ClienteItemReader reader,
                                 ClienteItemProcessor processor,
                                 ClienteItemWriter writer) {
        return new StepBuilder("fillClientesStep", jobRepository)
                // Chunk-Oriented Processing: Lee N elementos, los procesa individualmente
                // y escribe el bloque de 10 en una única transacción de base de datos.
                .<Cliente, Cliente>chunk(10, transactionManager)
                .reader(reader)       // Lector inyectado
                .processor(processor) // Procesador inyectado
                .writer(writer)       // Escritor inyectado
                .build();
    }

    // =========================================================================
    // 2. DEFINICIÓN DEL JOB (Trabajo Principal)
    // =========================================================================
    @Bean
    public Job fillClientesJob(JobRepository jobRepository, Step fillClientesStep) {
        return new JobBuilder("fillClientesJob", jobRepository)
                // RunIdIncrementer: Permite re-ejecutar el mismo Job varias veces
                // generando un nuevo parámetro 'run.id' secuencial en las tablas de metadatos.
                .incrementer(new RunIdIncrementer())
                .listener(jobExecutionListener()) // Listener para auditoría/logs
                .start(fillClientesStep)           // Primer (y único) paso
                .build();
    }

    // =========================================================================
    // 3. JOB EXECUTION LISTENER (Hooks de Ciclo de Vida)
    // Permite interceptar el inicio y fin del Job para trazabilidad y monitoreo.
    // =========================================================================
    private JobExecutionListener jobExecutionListener() {
        return new JobExecutionListener() {
            @Override
            public void beforeJob(JobExecution jobExecution) {
                log.info("=== Iniciando job: {} ===", jobExecution.getJobInstance().getJobName());
            }

            @Override
            public void afterJob(JobExecution jobExecution) {
                log.info("=== Job finalizado: {} | Estado: {} ===",
                        jobExecution.getJobInstance().getJobName(),
                        jobExecution.getStatus());
            }
        };
    }
}