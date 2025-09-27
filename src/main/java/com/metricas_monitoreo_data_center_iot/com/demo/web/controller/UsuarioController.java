package com.metricas_monitoreo_data_center_iot.com.demo.web.controller;


import com.metricas_monitoreo_data_center_iot.com.demo.enums.EstatusUsuario;
import com.metricas_monitoreo_data_center_iot.com.demo.persistence.entity.UsuarioEntity;
import com.metricas_monitoreo_data_center_iot.com.demo.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public List<UsuarioEntity> getAllUsuarios() {
        return usuarioService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioEntity> getUsuarioById(@PathVariable String id) {
        return usuarioService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/correo/{correo}")
    public ResponseEntity<UsuarioEntity> getUsuarioByCorreo(@PathVariable String correo) {
        return usuarioService.findByCorreo(correo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/rol/{nombreRol}")
    public List<UsuarioEntity> getUsuariosByRol(@PathVariable String nombreRol) {
        return usuarioService.findByRolNombre(nombreRol);
    }

    @PostMapping
    public ResponseEntity<?> createUsuario(@RequestBody UsuarioEntity usuario) {
        try {
            UsuarioEntity nuevoUsuario = usuarioService.save(usuario);
            return ResponseEntity.ok(nuevoUsuario);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUsuario(@PathVariable String id, @RequestBody UsuarioEntity usuarioDetails) {
        if (!usuarioService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        usuarioDetails.setIdUsuario(id);
        try {
            UsuarioEntity updatedUsuario = usuarioService.save(usuarioDetails);
            return ResponseEntity.ok(updatedUsuario);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/rol/{rolId}")
    public ResponseEntity<?> updateUsuarioRol(@PathVariable String id, @PathVariable Integer rolId) {
        try {
            UsuarioEntity usuario = usuarioService.actualizarRol(id, rolId);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable String id) {
        if (!usuarioService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        usuarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/existe/{correo}")
    public boolean verificarCorreoExiste(@PathVariable String correo) {
        return usuarioService.existsByCorreo(correo);
    }

    @PatchMapping("/{id}/desbloquear")
    public ResponseEntity<UsuarioEntity> desbloquearUsuario(@PathVariable String id) {
        try {
            UsuarioEntity usuario = usuarioService.desbloquearUsuario(id);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/estatus/{estatus}")
    public List<UsuarioEntity> getUsuariosPorEstatus(@PathVariable EstatusUsuario estatus) {
        return usuarioService.findByEstatus(estatus);
    }
}