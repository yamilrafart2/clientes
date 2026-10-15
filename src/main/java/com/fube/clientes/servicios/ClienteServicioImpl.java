package com.fube.clientes.servicios;

import com.fube.clientes.dto.ClienteDTO;
import com.fube.clientes.excepciones.RecursoNoEncontradoException;
import com.fube.clientes.mapper.ClienteMapper;
import com.fube.clientes.modelos.Cliente;
import com.fube.clientes.repositorio.ClienteRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * =============================================================================
 * CAPA DE SERVICIO: ClienteServicioImpl [Unidad 1 & 4]
 * =============================================================================
 *
 * CONTEXTO DE ARQUITECTURA:
 * Contiene la implementación de la lógica de negocio del módulo de Clientes.
 * Registrada como Spring Bean en el contenedor IoC mediante @Service.
 * Coordina las conversiones DTO <-> Entity a través de ClienteMapper y la
 * persistencia de datos mediante ClienteRepositorio.
 */
@Service
public class ClienteServicioImpl implements ClienteServicio {

    // Inyección de dependencias por constructor (Best Practice - Thread safe & immutable)
    private final ClienteRepositorio repositorio;

    public ClienteServicioImpl(ClienteRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    /**
     * Convierte el DTO recibido, lo persiste en PostgreSQL y retorna el DTO guardado con su ID.
     */
    @Override
    public ClienteDTO crear(ClienteDTO dto) {
        Cliente clienteANoPersistido = ClienteMapper.toEntity(dto);
        Cliente clienteGuardado = repositorio.save(clienteANoPersistido);
        return ClienteMapper.toDTO(clienteGuardado);
    }

    /**
     * Busca un cliente por su ID. Si no existe, lanza la excepción de dominio 'RecursoNoEncontradoException'.
     */
    @Override
    public ClienteDTO buscarPorId(Long id) {
        Cliente cliente = repositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con ID: " + id));
        return ClienteMapper.toDTO(cliente);
    }

    /**
     * Consulta todas las entidades de la BD, utiliza Streams de Java para transformar cada una a DTO
     * y retorna la lista inmutable.
     */
    @Override
    public List<ClienteDTO> buscarTodos() {
        return repositorio.findAll().stream()
                .map(ClienteMapper::toDTO)
                .toList();
    }

    /**
     * Actualiza la entidad en la BD utilizando el método save() de JPA (Merge).
     */
    @Override
    public ClienteDTO actualizar(ClienteDTO dto) {
        // Verificamos previamente la existencia para no crear un recurso nuevo por error
        if (dto.getId() == null || !repositorio.existsById(dto.getId())) {
            throw new RecursoNoEncontradoException("No se puede actualizar. Cliente no encontrado con ID: " + dto.getId());
        }
        Cliente clienteAActualizar = ClienteMapper.toEntity(dto);
        Cliente clienteActualizado = repositorio.save(clienteAActualizar);
        return ClienteMapper.toDTO(clienteActualizado);
    }

    /**
     * Elimina el cliente por su ID de la base de datos.
     */
    @Override
    public void eliminar(Long id) {
        if (!repositorio.existsById(id)) {
            throw new RecursoNoEncontradoException("No se puede eliminar. Cliente no encontrado con ID: " + id);
        }
        repositorio.deleteById(id);
    }
}