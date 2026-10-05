-- =============================================================================
-- MIGRACIÓN FLYWAY V1: Creación de la Tabla de Dominio 'clientes' [Unidad 1]
-- Nomenclatura del archivo: V1__create_clientes_table.sql (Doble guion bajo mandatory)
-- Engine objetivo: PostgreSQL 16+ [Unidad 1 / Docker Desktop]
-- =============================================================================

CREATE TABLE clientes (
    -- Clave Primaria (Primary Key) con autoincremento serial de 64 bits (8 bytes).
    -- Mapea directamente con '@Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;'
                          id         BIGSERIAL PRIMARY KEY,

    -- Restricción NOT NULL (Obligatorio). Mapea con '@Column(nullable = false) private String nombre;'
                          nombre     VARCHAR(255) NOT NULL,

    -- Restricción NOT NULL (Obligatorio). Mapea con '@Column(nullable = false) private String apellido;'
                          apellido   VARCHAR(255) NOT NULL,

    -- Restricción NOT NULL y UNIQUE (Unicidad a nivel base de datos).
    -- Mapea con '@Column(nullable = false, unique = true) private String email;'
                          email      VARCHAR(255) NOT NULL UNIQUE,

    -- Columna opcional (admite NULL). Mapea con '@Column private String telefono;'
                          telefono   VARCHAR(50),

    -- Columna opcional (admite NULL). Mapea con '@Column private String direccion;'
                          direccion  VARCHAR(255)
);