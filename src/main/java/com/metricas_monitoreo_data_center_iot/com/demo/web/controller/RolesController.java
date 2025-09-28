package com.metricas_monitoreo_data_center_iot.com.demo.web.controller;

import com.metricas_monitoreo_data_center_iot.com.demo.persistence.entity.RolesEntity;
import com.metricas_monitoreo_data_center_iot.com.demo.persistence.entity.UsuarioEntity;
import com.metricas_monitoreo_data_center_iot.com.demo.service.RolesService;
import com.metricas_monitoreo_data_center_iot.com.demo.service.dto.RolesDTO;
import com.metricas_monitoreo_data_center_iot.com.demo.service.dto.UsuarioDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class RolesController {

    @Autowired
    private RolesService rolesService;

    @GetMapping
    public ResponseEntity<List<RolesDTO>> getAllRoles() {
        List<RolesDTO> roles = rolesService.obtenerTodosLosRoles().stream().map(RolesDTO::new).toList();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RolesDTO> getRolPorId(@PathVariable Integer id) {
        Optional<RolesEntity> rol = rolesService.obtenerRolPorId(id);
        return rol.map(RolesDTO::new).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/nombre/{nombreRol}")
    public ResponseEntity<RolesDTO> getRolPorNombre(@PathVariable String nombreRol) {
        Optional<RolesEntity> rol = rolesService.obtenerRolPorNombre(nombreRol);
        return rol.map(RolesDTO::new).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/usuarios")
    public ResponseEntity<List<UsuarioDTO>> getUsuariosPorRol(@PathVariable Integer id) {
        Optional<RolesEntity> rol = rolesService.obtenerRolPorId(id);
        if (rol.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<UsuarioDTO> usuarios = rolesService.obtenerUsuariosPorRol(rol.get()).stream().map(UsuarioDTO::new).toList();
        return ResponseEntity.ok(usuarios);
    }

    @PostMapping
    public ResponseEntity<RolesDTO> crearRol(@RequestBody RolesEntity rol) {
        try {
            RolesEntity nuevoRol = rolesService.crearRol(rol);
            return ResponseEntity.ok(RolesDTO.crearSimple(nuevoRol));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarRol(@PathVariable Integer id) {
        try {
            rolesService.eliminarRol(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
