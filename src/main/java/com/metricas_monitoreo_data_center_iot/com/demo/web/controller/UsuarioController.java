package com.metricas_monitoreo_data_center_iot.com.demo.web.controller;


import com.metricas_monitoreo_data_center_iot.com.demo.enums.EstatusUsuario;
import com.metricas_monitoreo_data_center_iot.com.demo.persistence.entity.MaquinaEntity;
import com.metricas_monitoreo_data_center_iot.com.demo.persistence.entity.UsuarioEntity;
import com.metricas_monitoreo_data_center_iot.com.demo.service.MaquinaService;
import com.metricas_monitoreo_data_center_iot.com.demo.service.UsuarioService;
import com.metricas_monitoreo_data_center_iot.com.demo.service.dto.MaquinaDTO;
import com.metricas_monitoreo_data_center_iot.com.demo.service.dto.UsuarioDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private MaquinaService maquinaService;


    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> getAllUsuarios() {
        List<UsuarioEntity> usuarios = usuarioService.findAll();
        List<UsuarioDTO> usuariosDTO = usuarios.stream()
                .map(UsuarioDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(usuariosDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> getUsuarioById(@PathVariable String id) {
        return usuarioService.findById(id)
                .map(UsuarioDTO::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/correo/{correo}")
    public ResponseEntity<UsuarioDTO> getUsuarioByCorreo(@PathVariable String correo) {
        return usuarioService.findByCorreo(correo)
                .map(UsuarioDTO::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/rol/{nombreRol}")
    public List<UsuarioDTO> getUsuariosByRol(@PathVariable String nombreRol) {
        return usuarioService.findByRolNombre(nombreRol).stream().map(UsuarioDTO::new).toList();
    }

    @PostMapping
    public ResponseEntity<?> createUsuario(@RequestBody UsuarioEntity usuario) {
        try {
            UsuarioEntity nuevoUsuario = usuarioService.save(usuario);
            return ResponseEntity.ok(new UsuarioDTO(nuevoUsuario));
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
            return ResponseEntity.ok(new UsuarioDTO(updatedUsuario));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/rol/{rolId}")
    public ResponseEntity<?> updateUsuarioRol(@PathVariable String id, @PathVariable Integer rolId) {
        try {
            UsuarioEntity usuario = usuarioService.actualizarRol(id, rolId);
            return ResponseEntity.ok(new UsuarioDTO(usuario));
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
    public ResponseEntity<UsuarioDTO> desbloquearUsuario(@PathVariable String id) {
        try {
            UsuarioEntity usuario = usuarioService.desbloquearUsuario(id);
            return ResponseEntity.ok(new UsuarioDTO(usuario));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USUARIO')")
    @GetMapping("/{id}/maquinas")
    public ResponseEntity<List<MaquinaDTO>> getMaquinasDeUsuario(@PathVariable String id) {
        try {
            Optional<UsuarioEntity> usuarioOpt = usuarioService.findById(id);
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            List<MaquinaEntity> maquinas = maquinaService.findByResponsable(usuarioOpt.get());
            List<MaquinaDTO> maquinasDTO = maquinas.stream()
                    .map(MaquinaDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(maquinasDTO);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }


    @GetMapping("/estatus/{estatus}")
    public List<UsuarioDTO> getUsuariosPorEstatus(@PathVariable EstatusUsuario estatus) {
        return usuarioService.findByEstatus(estatus).stream().map(UsuarioDTO::new).toList();
    }
}