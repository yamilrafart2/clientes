-- =============================================================================
-- SCRIPT DE LIMPIEZA DDL: Tablas y Secuencias del Metamodelo de Spring Batch
-- Uso desde terminal: psql -h <host> -U <user> -d <db> -f scripts/drop-batch-tables.sql
-- Motor: PostgreSQL 16+ [Docker Desktop]
-- =============================================================================

-- =============================================================================
-- 1. ELIMINACIÓN DE TABLAS DE METADATOS (Inversa al orden de Claves Foráneas)
-- Usamos 'CASCADE' para eliminar automáticamente cualquier restricción DDL asociada.
-- =============================================================================

-- Tablas de contexto de ejecución (Parámetros y estado de steps/jobs)
DROP TABLE IF EXISTS BATCH_STEP_EXECUTION_CONTEXT CASCADE;
DROP TABLE IF EXISTS BATCH_JOB_EXECUTION_CONTEXT CASCADE;

-- Tablas de ejecución
DROP TABLE IF EXISTS BATCH_STEP_EXECUTION CASCADE;
DROP TABLE IF EXISTS BATCH_JOB_EXECUTION_PARAMS CASCADE;

-- Tabla de historial de ejecuciones de Jobs
DROP TABLE IF EXISTS BATCH_JOB_EXECUTION CASCADE;

-- Tabla raíz de instancias de Jobs (JobInstance)
DROP TABLE IF EXISTS BATCH_JOB_INSTANCE CASCADE;

-- =============================================================================
-- 2. ELIMINACIÓN DE SECUENCIAS AUTOINCREMENTALES
-- =============================================================================
DROP SEQUENCE IF EXISTS BATCH_STEP_EXECUTION_SEQ;
DROP SEQUENCE IF EXISTS BATCH_JOB_EXECUTION_SEQ;
DROP SEQUENCE IF EXISTS BATCH_JOB_SEQ;