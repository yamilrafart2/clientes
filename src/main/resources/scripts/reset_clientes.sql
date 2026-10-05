-- =============================================================================
-- SCRIPT DE RESETEO DE DATOS (DATA RESET UTILITY)
-- Restablece los campos 'telefono' y 'direccion' al estado con datos nulos
-- equivalente al seed inicial (V2__seed_clientes.sql) [Unidad 3].
--
-- Caso de Uso: Permite volver a ejecutar la prueba del proceso en lotes (Spring Batch)
-- tantas veces como sea necesario sin necesidad de recrear el contenedor de Postgres.
-- =============================================================================

-- Asigna 'telefono' a NULL para todos los registros cuya clave primaria (id) sea de la forma (3k + 1)
-- Ejemplo: IDs 1, 4, 7, 10...
UPDATE clientes SET telefono = NULL WHERE id % 3 = 1;

-- Asigna 'direccion' a NULL para todos los registros cuya clave primaria (id) sea de la forma (3k + 2)
-- Ejemplo: IDs 2, 5, 8, 11...
UPDATE clientes SET direccion = NULL WHERE id % 3 = 2;

-- Asigna 'telefono' y 'direccion' a NULL para todos los registros cuya clave primaria sea múltiplo de 3
-- Ejemplo: IDs 3, 6, 9, 12...
UPDATE clientes SET telefono = NULL, direccion = NULL WHERE id % 3 = 0;