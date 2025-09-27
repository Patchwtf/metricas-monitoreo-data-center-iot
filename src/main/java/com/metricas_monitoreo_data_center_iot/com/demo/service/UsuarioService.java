package com.metricas_monitoreo_data_center_iot.com.demo.service;

import com.metricas_monitoreo_data_center_iot.com.demo.enums.EstatusUsuario;
import com.metricas_monitoreo_data_center_iot.com.demo.persistence.entity.RolesEntity;
import com.metricas_monitoreo_data_center_iot.com.demo.persistence.entity.UsuarioEntity;
import com.metricas_monitoreo_data_center_iot.com.demo.persistence.repository.RolesRepository;
import com.metricas_monitoreo_data_center_iot.com.demo.persistence.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolesRepository rolesRepository;

    private static final int MAX_INTENTOS_LOGIN = 3;

    public List<UsuarioEntity> findAll() {
        return usuarioRepository.findAll();
    }

    public Optional<UsuarioEntity> findById(String id) {
        return usuarioRepository.findById(id);
    }

    public UsuarioEntity save(UsuarioEntity usuario) {
        if (usuario.getIdUsuario() == null && usuarioRepository.existsByCorreo(usuario.getCorreo())) {
            throw new RuntimeException("Ya existe un usuario con el correo: " + usuario.getCorreo());
        }
        if (usuario.getIdUsuario() == null) {
            if (usuario.getEstatus() == null) {
                usuario.setEstatus(EstatusUsuario.ACTIVO);
            }
            if (usuario.getNumIntentos() == null) {
                usuario.setNumIntentos(0);
            }
        }
        return usuarioRepository.save(usuario);
    }

    public UsuarioEntity desbloquearUsuario(String usuarioId) {
        UsuarioEntity usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setEstatus(EstatusUsuario.ACTIVO);
        usuario.setNumIntentos(0);
        return usuarioRepository.save(usuario);
    }

    public List<UsuarioEntity> findByEstatus(EstatusUsuario estatus) {
        return usuarioRepository.findByEstatus(estatus);
    }

    public void deleteById(String id) {
        usuarioRepository.deleteById(id);
    }

    public Optional<UsuarioEntity> findByCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    public boolean existsByCorreo(String correo) {
        return usuarioRepository.existsByCorreo(correo);
    }

    public List<UsuarioEntity> findByRolNombre(String nombreRol) {
        return usuarioRepository.findByRol_NombreRol(nombreRol);
    }

    public List<UsuarioEntity> findByRol(RolesEntity rol) {
        return usuarioRepository.findByRol(rol);
    }

    public UsuarioEntity actualizarRol(String usuarioId, Integer nuevoRolId) {
        UsuarioEntity usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        RolesEntity nuevoRol = rolesRepository.findById(nuevoRolId)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        usuario.setRol(nuevoRol);
        return usuarioRepository.save(usuario);
    }
}