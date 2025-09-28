package com.metricas_monitoreo_data_center_iot.com.demo.security.service;

import com.metricas_monitoreo_data_center_iot.com.demo.enums.EstatusUsuario;
import com.metricas_monitoreo_data_center_iot.com.demo.persistence.entity.UsuarioEntity;
import com.metricas_monitoreo_data_center_iot.com.demo.persistence.repository.UsuarioRepository;
import com.metricas_monitoreo_data_center_iot.com.demo.security.dto.AuthRequest;
import com.metricas_monitoreo_data_center_iot.com.demo.security.dto.AuthResponse;
import com.metricas_monitoreo_data_center_iot.com.demo.security.jwt.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class AutenticacionService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${app.security.max-attempts:5}")
    private int maxAttempts;

    public AuthResponse authenticateUser(AuthRequest request) {
        UsuarioEntity usuario = usuarioRepository.findByCorreo(request.getCorreo())
                .orElseThrow(() -> {
                    return new BadCredentialsException("Credenciales inválidas");
                });

        if (usuario.getEstatus() == EstatusUsuario.BLOQUEADO) {
            throw new LockedException("Usuario bloqueado. Contacte al administrador.");
        }

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            handleFailedAttempt(usuario);
            throw new BadCredentialsException("Credenciales inválidas");
        }

        handleSuccessfulAttempt(usuario);

        List<String> roles = Collections.singletonList(usuario.getRol().getNombreRol());
        String token = jwtUtil.generateToken(usuario.getCorreo(), roles, usuario.getTokenVersion());

        Date expiresAt = jwtUtil.getExpirationDateFromToken(token);

        return new AuthResponse(token, "Bearer",usuario.getCorreo(), roles, usuario.getEstatus(), expiresAt);
    }

    private void handleFailedAttempt(UsuarioEntity usuario) {
        usuario.setNumIntentos(usuario.getNumIntentos() + 1);
        if (usuario.getNumIntentos() >= maxAttempts) {
            usuario.setEstatus(EstatusUsuario.BLOQUEADO);
        }
        usuarioRepository.save(usuario);
    }

    private void handleSuccessfulAttempt(UsuarioEntity usuario) {
        System.out.println("SESION BUENA!!!");
        usuario.setNumIntentos(0);
        usuarioRepository.save(usuario);
    }
}
