package com.metricas_monitoreo_data_center_iot.com.demo.service;

import com.metricas_monitoreo_data_center_iot.com.demo.enums.EstatusUsuario;
import com.metricas_monitoreo_data_center_iot.com.demo.persistence.entity.UsuarioEntity;
import com.metricas_monitoreo_data_center_iot.com.demo.persistence.repository.UsuarioRepository;
import com.metricas_monitoreo_data_center_iot.com.demo.service.dto.ResultadoLogin;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class AutenticacionService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    private static final int MAX_INTENTOS_LOGIN = 3;

    public ResultadoLogin verificarCredenciales(String correo, String passwordIngresada) {
        Optional<UsuarioEntity> usuarioOpt = usuarioRepository.findByCorreo(correo);

        if (usuarioOpt.isEmpty()) {
            return ResultadoLogin.fallido("Credenciales inválidas", null, 0);
        }

        UsuarioEntity usuario = usuarioOpt.get();

        if (usuario.getEstatus() == EstatusUsuario.BLOQUEADO) {
            return ResultadoLogin.fallido("Usuario bloqueado. Contacte al administrador.",
                    EstatusUsuario.BLOQUEADO, 0);
        }

        if (usuario.getEstatus() == EstatusUsuario.DESHABILITADO) {
            return ResultadoLogin.fallido("Usuario deshabilitado", EstatusUsuario.DESHABILITADO, 0);
        }

        boolean credencialesCorrectas = simularVerificacionPassword(passwordIngresada, usuario.getPassword());

        if (credencialesCorrectas) {
            usuario.setNumIntentos(0);
            usuarioRepository.save(usuario);
            return ResultadoLogin.exitoso("Login exitoso");

        } else {
            return manejarIntentoFallido(usuario);
        }
    }

    private ResultadoLogin manejarIntentoFallido(UsuarioEntity usuario) {
        usuario.setNumIntentos(usuario.getNumIntentos() + 1);
        int intentosRestantes = MAX_INTENTOS_LOGIN - usuario.getNumIntentos();
        if (usuario.getNumIntentos() >= MAX_INTENTOS_LOGIN) {
            usuario.setEstatus(EstatusUsuario.BLOQUEADO);
            usuarioRepository.save(usuario);
            return ResultadoLogin.fallido("Usuario bloqueado. Contacte al administrador.",
                    EstatusUsuario.BLOQUEADO, 0);
        }
        usuarioRepository.save(usuario);
        return ResultadoLogin.fallido("Credenciales inválidas. ", usuario.getEstatus(), intentosRestantes);
    }

    public void desbloquearUsuario(String usuarioId) {
        UsuarioEntity usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setEstatus(EstatusUsuario.ACTIVO);
        usuario.setNumIntentos(0);
        usuarioRepository.save(usuario);
    }

    private boolean simularVerificacionPassword(String ingresada, String almacenada) {
        return ingresada != null && ingresada.equals(almacenada);
    }
}
