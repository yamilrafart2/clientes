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

}