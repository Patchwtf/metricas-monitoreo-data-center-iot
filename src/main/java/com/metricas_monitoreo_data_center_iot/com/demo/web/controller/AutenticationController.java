package com.metricas_monitoreo_data_center_iot.com.demo.web.controller;

import com.metricas_monitoreo_data_center_iot.com.demo.enums.EstatusUsuario;
import com.metricas_monitoreo_data_center_iot.com.demo.persistence.entity.RolesEntity;
import com.metricas_monitoreo_data_center_iot.com.demo.persistence.entity.UsuarioEntity;
import com.metricas_monitoreo_data_center_iot.com.demo.persistence.repository.UsuarioRepository;
import com.metricas_monitoreo_data_center_iot.com.demo.security.service.AutenticacionService;
import com.metricas_monitoreo_data_center_iot.com.demo.service.RolesService;
import com.metricas_monitoreo_data_center_iot.com.demo.security.dto.AuthRequest;
import com.metricas_monitoreo_data_center_iot.com.demo.security.dto.AuthResponse;
import com.metricas_monitoreo_data_center_iot.com.demo.security.dto.RegisterRequest;
import com.metricas_monitoreo_data_center_iot.com.demo.security.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
@Validated
public class AutenticationController {
    @Autowired
    private AutenticacionService authService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolesService rolesService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request) {
        try {
            AuthResponse response = authService.authenticateUser(request);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciales inválidas");
        } catch (LockedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Usuario bloqueado");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error en autenticación");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            if (usuarioRepository.existsByCorreo(request.getCorreo())) {
                return ResponseEntity.badRequest()
                        .body("El correo ya está registrado");
            }

            if (!isPasswordStrong(request.getPassword())) {
                return ResponseEntity.badRequest()
                        .body("La contraseña debe tener al menos 15 caracteres, incluir mayúsculas, minúsculas y números");
            }

            RolesEntity rolUsuario = request.getRol() == null ? rolesService.obtenerRolPorId(3)
                    .orElseThrow(() -> new RuntimeException("Rol USUARIO no encontrado")) :
                    rolesService.obtenerRolPorId(request.getRol())
                            .orElseThrow(() -> new RuntimeException("Rol USUARIO no encontrado"));

            UsuarioEntity usuario = new UsuarioEntity();
            usuario.setNombre(request.getNombre());
            usuario.setApellido(request.getApellido());
            usuario.setCorreo(request.getCorreo());
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
            usuario.setEstatus(EstatusUsuario.ACTIVO);
            usuario.setRol(rolUsuario);
            usuario.setNumIntentos(0);

            usuarioRepository.save(usuario);
            return ResponseEntity.ok("Usuario registrado exitosamente");

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Error en el registro");
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);

            if (token == null) {
                return ResponseEntity.badRequest().body("{\"error\": \"Token no proporcionado\"}");
            }

            String username = jwtUtil.extractUsername(token);
            UsuarioEntity usuario = usuarioRepository.findByCorreo(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            usuario.invalidarTokensAnteriores();
            usuarioRepository.save(usuario);

            return ResponseEntity.ok("{\"message\": \"Sesión cerrada exitosamente. Todos los tokens anteriores fueron invalidados.\"}");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Error al cerrar sesión}");
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/token-info")
    public ResponseEntity<?> getTokenInfo(HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            if (token == null) {
                return ResponseEntity.badRequest().body("{\"error\": \"Token no proporcionado\"}");
            }
            String username = jwtUtil.extractUsername(token);
            UsuarioEntity usuario = usuarioRepository.findByCorreo(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            Map<String, Object> info = new HashMap<>();
            info.put("usuario", username);
            info.put("tokenValido", jwtUtil.validateToken(token));
            info.put("versionToken", jwtUtil.extractTokenVersion(token));
            info.put("versionActual", usuario.getTokenVersion());
            info.put("versionCoincide", jwtUtil.isTokenVersionValid(token, usuario.getTokenVersion()));
            info.put("expiracion", jwtUtil.getExpirationDateFromToken(token));
            return ResponseEntity.ok(info);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"error\": \"Token inválido\"}");
        }
    }
    private boolean isPasswordStrong(String password) {
        String pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{15,}$";
        return password.matches(pattern);
    }
}