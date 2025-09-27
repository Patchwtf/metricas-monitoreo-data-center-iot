package com.metricas_monitoreo_data_center_iot.com.demo.web.controller;

import com.metricas_monitoreo_data_center_iot.com.demo.persistence.entity.RolesEntity;
import com.metricas_monitoreo_data_center_iot.com.demo.persistence.entity.UsuarioEntity;
import com.metricas_monitoreo_data_center_iot.com.demo.service.RolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "*")
public class RolesController {

    @Autowired
    private RolesService rolesService;

    @GetMapping
    public ResponseEntity<List<RolesEntity>> getAllRoles() {
        List<RolesEntity> roles = rolesService.obtenerTodosLosRoles();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RolesEntity> getRolPorId(@PathVariable Integer id) {
        Optional<RolesEntity> rol = rolesService.obtenerRolPorId(id);
        return rol.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/nombre/{nombreRol}")
    public ResponseEntity<RolesEntity> getRolPorNombre(@PathVariable String nombreRol) {
        Optional<RolesEntity> rol = rolesService.obtenerRolPorNombre(nombreRol);
        return rol.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/usuarios")
    public ResponseEntity<List<UsuarioEntity>> getUsuariosPorRol(@PathVariable Integer id) {
        Optional<RolesEntity> rol = rolesService.obtenerRolPorId(id);
        if (rol.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<UsuarioEntity> usuarios = rolesService.obtenerUsuariosPorRol(rol.get());
        return ResponseEntity.ok(usuarios);
    }

    @PostMapping
    public ResponseEntity<RolesEntity> crearRol(@RequestBody RolesEntity rol) {
        try {
            RolesEntity nuevoRol = rolesService.crearRol(rol);
            return ResponseEntity.ok(nuevoRol);
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
