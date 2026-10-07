package com.fube.clientes.batch;

import com.fube.clientes.dto.ClienteCsvDTO;
import com.fube.clientes.modelos.Cliente;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Clase de configuración del Job de Spring Batch para la importación desde CSV.
 * Define los Beans necesarios para el Reader, Writer, Listener, Step y Job.
 */
@Slf4j
@Configuration // Declaración de clase de configuración de Spring Boot.
@RequiredArgsConstructor
public class ClienteCsvBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final ClienteCsvItemProcessor clienteCsvItemProcessor;

    /**
     * Reader: Lee el archivo 'clientes.csv' ubicado en classpath (src/main/resources/).
     */
    @Bean
    public FlatFileItemReader<ClienteCsvDTO> clienteCsvItemReader() {
        return new FlatFileItemReaderBuilder<ClienteCsvDTO>()
                .name("clienteCsvItemReader")
                .resource(new ClassPathResource("clientes.csv")) // Recurso de entrada.
                .linesToSkip(1) // Saltea la primera fila correspondiente a los encabezados.
                .delimited() // Indica formato delimitado (por defecto comas ',').
                .names("nombre", "apellido", "email", "telefono", "direccion") // Mapeo de columnas.
                .targetType(ClienteCsvDTO.class) // Clase DTO de destino.
                .build();
    }

    /**
     * Writer: Persiste las entidades 'Cliente' en la base de datos PostgreSQL usando JPA.
     */
    @Bean
    public JpaItemWriter<Cliente> clienteJpaItemWriter() {
        return new JpaItemWriterBuilder<Cliente>()
                .entityManagerFactory(entityManagerFactory) // Requiere el EntityManagerFactory de JPA.
                .build();
    }

    /**
     * Listener: Captura eventos del ciclo de vida del Job para loguear el resumen al finalizar.
     */
    @Bean
    public JobExecutionListener importClienteJobListener() {
        return new JobExecutionListener() {
            @Override
            public void afterJob(JobExecution jobExecution) {
                // Obtenemos las métricas del Step ejecutado
                jobExecution.getStepExecutions().forEach(stepExecution -> {
                    long readCount = stepExecution.getReadCount();     // Leídos del CSV
                    long writeCount = stepExecution.getWriteCount();   // Guardados en PostgreSQL
                    long filterCount = stepExecution.getFilterCount(); // Descartados por el Processor

                    log.info("=================================================");
                    log.info("RESUMEN DE EJECUCIÓN DEL JOB DE IMPORTACIÓN CSV");
                    log.info("Total clientes leídos del CSV: {}", readCount);
                    log.info("Total clientes guardados en BD: {}", writeCount);
                    log.info("Total clientes descartados (duplicados): {}", filterCount);
                    log.info("=================================================");
                });
            }
        };
    }

    /**
     * Step: Define la unidad de procesamiento por lotes en bloques (chunks) de a 10 ítems.
     */
    @Bean
    public Step importClienteCsvStep() {
        return new StepBuilder("importClienteCsvStep", jobRepository)
                .<ClienteCsvDTO, Cliente>chunk(10, transactionManager) // Lectura DTO -> Escritura Cliente en chunks de 10
                .reader(clienteCsvItemReader())
                .processor(clienteCsvItemProcessor)
                .writer(clienteJpaItemWriter())
                .build();
    }

    /**
     * Job: Orquestador principal que ejecuta el Step de importación.
     */
    @Bean(name = "importClienteCsvJob")
    public Job importClienteCsvJob() {
        return new JobBuilder("importClienteCsvJob", jobRepository)
                .incrementer(new RunIdIncrementer()) // Genera IDs únicos para reejecutar el Job sin conflictos de metadatos.
                .listener(importClienteJobListener())
                .start(importClienteCsvStep())
                .build();
    }
}