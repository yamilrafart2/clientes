package com.fube.clientes.modelos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// =============================================================================
// 1. ANOTACIONES JPA (PERSISTENCIA Y MAPEO ORM) [Unidad 1]
// =============================================================================

// Marca esta clase Java como una entidad manejada por JPA/Hibernate.
// JPA creará un mapeo Objeto-Relacional (ORM) entre los atributos de la clase
// y las columnas de la tabla en PostgreSQL.
@Entity

// Especifica explícitamente el nombre de la tabla en la BD ('clientes').
// Si no se usara @Table, Hibernate nombraría la tabla como 'cliente' por defecto.
@Table(name = "clientes")

// =============================================================================
// 2. ANOTACIONES DE PROJECT LOMBOK (BOILERPLATE REDUCTION) [Unidad 1]
// =============================================================================

// @Data: Genera automáticamente en bytecode:
// - Getters para todos los campos.
// - Setters para campos no finales.
// - toString(), equals() y hashCode().
// - RequiredArgsConstructor (para campos final/non-null).
@Data

// Genera un constructor con todos los argumentos (id, nombre, apellido, etc.).
@AllArgsConstructor

// Genera un constructor vacío (sin argumentos).
// ¡OBLIGATORIO PARA JPA! Hibernate requiere un constructor no-args público o
// protegido para instanciar el objeto mediante reflexión al consultar la BD.
@NoArgsConstructor

// Aplica el patrón de diseño Creacional "Builder" (Gof).
// Permite instanciar objetos con una sintaxis fluida y legible:
// Cliente.builder().nombre("Yamil").email("...").build();
@Builder
public class Cliente {

    // =========================================================================
    // 3. MAPEO DE ATRIBUTOS A COLUMNAS EN POSTGRESQL
    // =========================================================================

    // Declara que este campo es la clave primaria (Primary Key) de la entidad.
    @Id

    // IDENTITY: Delega la generación del ID a la base de datos PostgreSQL
    // mediante una columna de tipo auto-incremental (SERIAL o BIGSERIAL en Postgres).
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Columna obligatoria (NOT NULL) en la base de datos.
    @Column(nullable = false)
    private String nombre;

    // Columna obligatoria (NOT NULL) en la base de datos.
    @Column(nullable = false)
    private String apellido;

    // Columna obligatoria (NOT NULL) y con restricción de UNICIDAD (UNIQUE CONSTRAINT).
    // PostgreSQL rechazará cualquier intento de insertar un email duplicado.
    @Column(nullable = false, unique = true)
    private String email;

    // Columna opcional (permite NULL).
    @Column
    private String telefono;

    // Columna opcional (permite NULL).
    @Column
    private String direccion;

}