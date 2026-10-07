package com.fube.clientes.batch;

import com.fube.clientes.dto.ClienteExportCsvDTO;
import com.fube.clientes.modelos.Cliente;
import com.fube.clientes.repositorio.ClienteRepositorio;
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
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Collections;
import java.util.Map;

/**
 * Configuracion del Job de Spring Batch para exportar clientes desde PostgreSQL hacia CSV.
 * Ensambla el RepositoryItemReader, FlatFileItemWriter, Step y Job.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class ClienteExportBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final ClienteRepositorio clienteRepositorio;
    private final ClienteExportItemProcessor clienteExportItemProcessor;

    /**
     * Reader: Lee paginado desde PostgreSQL utilizando el repositorio Spring Data JPA.
     * Exige un Sort explicito para asegurar un orden deterministico entre paginas.
     */
    @Bean
    public RepositoryItemReader<Cliente> clienteRepositoryItemReader() {
        return new RepositoryItemReaderBuilder<Cliente>()
                .name("clienteRepositoryItemReader")
                .repository(clienteRepositorio)
                .methodName("findAll") // Metodo estandar de JpaRepository
                .pageSize(10)          // Tamaña de la pagina de lectura de base de datos
                .sorts(Map.of("id", Sort.Direction.ASC)) // Orden estricto requerido
                .build();
    }

    /**
     * Writer: Genera el archivo plano 'clientes_exportados.csv' en la raiz del proyecto.
     */
    @Bean
    public FlatFileItemWriter<ClienteExportCsvDTO> clienteFlatFileItemWriter() {
        // Mapea las propiedades del DTO hacia las columnas del CSV en el orden exacto deseado
        BeanWrapperFieldExtractor<ClienteExportCsvDTO> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[]{"id", "nombre", "apellido", "email", "telefono", "direccion"});

        // Une los campos mediante un delimitador de coma ','
        DelimitedLineAggregator<ClienteExportCsvDTO> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(",");
        lineAggregator.setFieldExtractor(fieldExtractor);

        return new FlatFileItemWriterBuilder<ClienteExportCsvDTO>()
                .name("clienteFlatFileItemWriter")
                .resource(new FileSystemResource("clientes_exportados.csv")) // Archivo de salida
                .headerCallback(writer -> writer.write("id,nombre,apellido,email,telefono,direccion")) // Primera fila con encabezados
                .lineAggregator(lineAggregator)
                .build();
    }

    /**
     * Listener: Captura metricas al finalizar el Job para loguear la cantidad total exportada
     * y los descartados por falta de direccion (Requerimiento Extra Ejercicio 2).
     */
    @Bean
    public JobExecutionListener exportClienteJobListener() {
        return new JobExecutionListener() {
            @Override
            public void afterJob(JobExecution jobExecution) {
                jobExecution.getStepExecutions().forEach(stepExecution -> {
                    long readCount = stepExecution.getReadCount();     // Leidos de PostgreSQL
                    long writeCount = stepExecution.getWriteCount();   // Exportados al CSV
                    long filterCount = stepExecution.getFilterCount(); // Excluidos por no tener direccion

                    log.info("=================================================");
                    log.info("RESUMEN DE EJECUCIÓN DEL JOB DE EXPORTACIÓN CSV (EXTRA)");
                    log.info("Total clientes leídos de la BD: {}", readCount);
                    log.info("Total clientes exportados al CSV: {}", writeCount);
                    log.info("Total clientes excluidos por falta de dirección: {}", filterCount);
                    log.info("=================================================");
                });
            }
        };
    }

    /**
     * Step: Procesa en bloques (chunks) de a 10 elementos de la BD al CSV.
     */
    @Bean
    public Step exportClienteCsvStep() {
        return new StepBuilder("exportClienteCsvStep", jobRepository)
                .<Cliente, ClienteExportCsvDTO>chunk(10, transactionManager)
                .reader(clienteRepositoryItemReader())
                .processor(clienteExportItemProcessor)
                .writer(clienteFlatFileItemWriter())
                .build();
    }

    /**
     * Job: Orquestador principal de la exportación a CSV.
     */
    @Bean(name = "exportClienteCsvJob")
    public Job exportClienteCsvJob() {
        return new JobBuilder("exportClienteCsvJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(exportClienteJobListener())
                .start(exportClienteCsvStep())
                .build();
    }
}