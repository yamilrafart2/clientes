-- =============================================================================
-- SCRIPT DE PRECARGA PARA PRUEBAS DE PERSISTENCIA: Unit 4 - Bloque D
-- Ubicación: src/test/resources/import-clientes-incompletos.sql
-- Propósito: Inserta un conjunto de datos sintéticos controlados para evaluar
--            consultas personalizadas y derivadas mediante la anotación @Sql.
-- =============================================================================

-- Registro 1: Cliente completo (No debe ser retornado por findByTelefonoIsNullOrDireccionIsNull)
INSERT INTO clientes (id, nombre, apellido, email, telefono, direccion)
VALUES (100, 'Carlos', 'Santi', 'carlos@mail.com', '3424000000', 'Calle 10');

-- Registro 2: Cliente incompleto (Teléfono NULL -> Debe ser retornado)
INSERT INTO clientes (id, nombre, apellido, email, telefono, direccion)
VALUES (101, 'Ana', 'Rios', 'ana@mail.com', NULL, 'Calle 20');

-- Registro 3: Cliente incompleto (Dirección NULL -> Debe ser retornado)
INSERT INTO clientes (id, nombre, apellido, email, telefono, direccion)
VALUES (102, 'Luis', 'Sosa', 'luis@mail.com', '3424888888', NULL);