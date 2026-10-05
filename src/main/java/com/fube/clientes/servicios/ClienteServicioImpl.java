package com.fube.clientes.servicios;

import com.fube.clientes.dto.ClienteDTO;
import com.fube.clientes.mapper.ClienteMapper;
import com.fube.clientes.modelos.Cliente;
import com.fube.clientes.repositorio.ClienteRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;

// =============================================================================
// ANOTACIÓN @Service Y BEAN MANAGEMENT [Unidad 1]
// Indica a Spring IoC Container que esta clase contiene la lógica de negocio
// y debe registrarse como un Bean administrado en el contexto de la aplicación.
// =============================================================================
@Service
public class ClienteServicioImpl implements ClienteServicio {

    // -------------------------------------------------------------------------
    // INYECCIÓN DE DEPENDENCIAS POR CONSTRUCTOR (Clean Code & Best Practice)
    // Se declara 'final' para garantizar la inmutabilidad de la dependencia.
    // Nota: Aunque no lleve @Autowired explícito, Spring 4.3+ inyecta
    // automáticamente cuando hay un único constructor.
    // -------------------------------------------------------------------------
    private final ClienteRepositorio repositorio;

    public ClienteServicioImpl(ClienteRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    // =========================================================================
    // CREAR CLIENTE
    // Persiste la entidad en Postgres y mapea el resultado a DTO.
    // =========================================================================
    @Override
    public ClienteDTO crear(Cliente cliente) {
        return ClienteMapper.toDTO(repositorio.save(cliente));
    }

    // =========================================================================
    // BUSCAR POR ID (Manejo de Excepciones y Functional Programming)
    // findById retorna un Optional<Cliente>.
    // Se utiliza .map() para transformar la entidad si existe, o orElseThrow()
    // para lanzar una excepción si el registro no fue encontrado.
    // =========================================================================
    @Override
    public ClienteDTO buscarPorId(Long id) {
        return repositorio.findById(id)
                .map(ClienteMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + id));
    }

    // =========================================================================
    // BUSCAR TODOS
    // Consulta la BD, convierte el resultado a un Stream funcional, mapea
    // cada entidad a DTO y retorna la lista inmutable.
    // =========================================================================
    @Override
    public List<ClienteDTO> buscarTodos() {
        return repositorio.findAll().stream()
                .map(ClienteMapper::toDTO)
                .toList();
    }

    // =========================================================================
    // ACTUALIZAR CLIENTE
    // Reutiliza save(), el cual en JPA realiza un 'merge' si el ID ya existe en la BD.
    // =========================================================================
    @Override
    public ClienteDTO actualizar(Cliente cliente) {
        return ClienteMapper.toDTO(repositorio.save(cliente));
    }

    // =========================================================================
    // ELIMINAR CLIENTE
    // Elimina la fila correspondiente por su clave primaria.
    // =========================================================================
    @Override
    public void eliminar(Long id) {
        repositorio.deleteById(id);
    }
}