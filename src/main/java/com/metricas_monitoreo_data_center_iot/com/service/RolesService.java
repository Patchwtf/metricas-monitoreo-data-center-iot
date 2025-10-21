package com.metricas_monitoreo_data_center_iot.com.service;

import com.metricas_monitoreo_data_center_iot.com.persistence.entity.RolesEntity;
import com.metricas_monitoreo_data_center_iot.com.persistence.entity.UsuarioEntity;
import com.metricas_monitoreo_data_center_iot.com.persistence.repository.RolesRepository;
import com.metricas_monitoreo_data_center_iot.com.persistence.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RolesService {

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<RolesEntity> obtenerTodosLosRoles() {
        return rolesRepository.findAll();
    }

    public Optional<RolesEntity> obtenerRolPorId(Integer id) {
        return rolesRepository.findById(id);
    }

    public Optional<RolesEntity> obtenerRolPorNombre(String nombreRol) {
        return rolesRepository.findByNombreRol(nombreRol);
    }

    public List<UsuarioEntity> obtenerUsuariosPorRol(RolesEntity rol) {
        return usuarioRepository.findByRol(rol);
    }

    public RolesEntity crearRol(RolesEntity rol) {
        if (rolesRepository.existsByNombreRol(rol.getNombreRol())) {
            throw new RuntimeException("Ya existe un rol con el nombre: " + rol.getNombreRol());
        }
        return rolesRepository.save(rol);
    }

    public void eliminarRol(Integer id) {
        RolesEntity rol = rolesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        List<UsuarioEntity> usuariosConRol = usuarioRepository.findByRol(rol);
        if (!usuariosConRol.isEmpty()) {
            throw new RuntimeException("No se puede eliminar el rol, tiene usuarios asignados");
        }
        rolesRepository.deleteById(id);
    }
}
