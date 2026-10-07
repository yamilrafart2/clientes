package com.fube.clientes.repositorio;

import com.fube.clientes.modelos.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// =============================================================================
// ANOTACIÓN @Repository Y PATRÓN REPOSITORY [Unidad 1]
// Indica a Spring que esta interfaz es un componente de la capa de acceso a datos
// (DAO - Data Access Object).
// Spring Data JPA generará automáticamente la implementación en tiempo de ejecución
// mediante proxies dinámicos de Java.
// =============================================================================
@Repository
public interface ClienteRepositorio extends JpaRepository<Cliente, Long> {

    // =========================================================================
    // DERIVED QUERY METHOD (Método de Consulta Derivado) [Unidad 1]
    // Spring Data JPA lee el nombre del método y genera automáticamente
    // la sentencia SQL equivalente sin necesidad de escribir código ni JPQL.
    //
    // TRADUCCIÓN SQL AUTOMÁTICA DE HIBERNATE:
    // SELECT * FROM clientes
    // WHERE telefono IS NULL OR direccion IS NULL;
    // =========================================================================
    List<Cliente> findByTelefonoIsNullOrDireccionIsNull();

    /**
     * Comprueba la existencia de un registro en la tabla 'clientes' según el correo electrónico.
     *
     * Mapeo Derivado (Derived Query Method):
     * Spring Data JPA analiza el nombre del método en tiempo de inicio:
     *  - 'existsBy': Genera una consulta SQL optimizada de tipo 'SELECT EXISTS(SELECT 1 FROM clientes WHERE email = ?)'.
     *  - 'Email': Atributo de la entidad 'Cliente' sobre el que se aplica la restricción de igualdad (WHERE email = :email).
     *
     * Ventaja de Rendimiento (Tech Lead Insight):
     * Retorna un boolean directo sin instanciar la entidad en memoria RAM ni sobrecargar el ORM/Hibernate,
     * lo cual es vital durante ejecuciones masivas de Spring Batch.
     *
     * @param email Dirección de correo a verificar en PostgreSQL.
     * @return 'true' si el email ya existe en la base de datos; 'false' en caso contrario.
     */
    boolean existsByEmail(String email);
}