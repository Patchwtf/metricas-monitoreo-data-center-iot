package com.metricas_monitoreo_data_center_iot.com.demo.web.controller;

import com.metricas_monitoreo_data_center_iot.com.demo.enums.AccionesAccesos;
import com.metricas_monitoreo_data_center_iot.com.demo.persistence.entity.AccesosEntity;
import com.metricas_monitoreo_data_center_iot.com.demo.service.AccesosService;
import com.metricas_monitoreo_data_center_iot.com.demo.service.dto.RegistroAccesoRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accesos")
@CrossOrigin(origins = "*")
public class AccesosController {

    @Autowired
    private AccesosService accesosService;

    @GetMapping
    public ResponseEntity<List<AccesosEntity>> getAllAccesos() {
        List<AccesosEntity> accesos = accesosService.obtenerTodosLosAccesos();
        return ResponseEntity.ok(accesos);
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<AccesosEntity>> getAccesosPorUsuario(@PathVariable Integer idUsuario) {
        List<AccesosEntity> accesos = accesosService.obtenerAccesosPorUsuario(idUsuario);
        return ResponseEntity.ok(accesos);
    }

    @GetMapping("/rango")
    public ResponseEntity<List<AccesosEntity>> getAccesosPorRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        List<AccesosEntity> accesos = accesosService.obtenerAccesosPorRango(inicio, fin);
        return ResponseEntity.ok(accesos);
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Object>> getEstadisticasAccesos() {
        Map<String, Object> estadisticas = accesosService.obtenerEstadisticasAccesos();
        return ResponseEntity.ok(estadisticas);
    }

    @GetMapping("/sospechosos")
    public ResponseEntity<List<AccesosEntity>> getAccesosSospechosos() {
        List<AccesosEntity> sospechosos = accesosService.obtenerAccesosSospechosos();
        return ResponseEntity.ok(sospechosos);
    }

    @GetMapping("/accion/{accion}")
    public ResponseEntity<List<AccesosEntity>> getAccesosPorAccion(@PathVariable AccionesAccesos accion) {
        List<AccesosEntity> accesos = accesosService.obtenerAccesosPorAccion(accion);
        return ResponseEntity.ok(accesos);
    }

    @PostMapping
    public ResponseEntity<?> registrarAcceso(@RequestBody RegistroAccesoRequest request) {
        try {
            AccesosEntity acceso = accesosService.registrarAccesoValidado(request.getIdUsuario(), request.getAccion());
            return ResponseEntity.ok(acceso);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage(), "timestamp", LocalDateTime.now()));
        }
    }
}