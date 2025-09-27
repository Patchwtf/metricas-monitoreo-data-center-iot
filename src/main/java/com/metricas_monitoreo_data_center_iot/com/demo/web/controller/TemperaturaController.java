package com.metricas_monitoreo_data_center_iot.com.demo.web.controller;

import com.metricas_monitoreo_data_center_iot.com.demo.enums.EstatusTemperatura;
import com.metricas_monitoreo_data_center_iot.com.demo.persistence.entity.TemperaturaEntity;
import com.metricas_monitoreo_data_center_iot.com.demo.service.TemperaturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/temperaturas")
@CrossOrigin(origins = "*")
public class TemperaturaController {

    @Autowired
    private TemperaturaService temperaturaService;

    @GetMapping
    public ResponseEntity<List<TemperaturaEntity>> getAllTemperaturas() {
        List<TemperaturaEntity> temperaturas = temperaturaService.obtenerTodasLasTemperaturas();
        return ResponseEntity.ok(temperaturas);
    }

    @GetMapping("/ultimas")
    public ResponseEntity<List<TemperaturaEntity>> getUltimasTemperaturas() {
        List<TemperaturaEntity> temperaturas = temperaturaService.obtenerUltimasTemperaturas();
        return ResponseEntity.ok(temperaturas);
    }

    @GetMapping("/rango")
    public ResponseEntity<List<TemperaturaEntity>> getTemperaturasPorRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {

        List<TemperaturaEntity> temperaturas = temperaturaService.obtenerTemperaturasPorRango(inicio, fin);
        return ResponseEntity.ok(temperaturas);
    }

    @GetMapping("/estatus/{estatus}")
    public ResponseEntity<List<TemperaturaEntity>> getTemperaturasPorEstatus(@PathVariable EstatusTemperatura estatus) {
        List<TemperaturaEntity> temperaturas = temperaturaService.obtenerTemperaturasPorEstatus(estatus);
        return ResponseEntity.ok(temperaturas);
    }

    @GetMapping("/alertas")
    public ResponseEntity<List<TemperaturaEntity>> getAlertasTemperatura() {
        List<TemperaturaEntity> alertas = temperaturaService.obtenerAlertasTemperatura();
        return ResponseEntity.ok(alertas);
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Object>> getEstadisticasTemperatura(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {

        Map<String, Object> estadisticas = temperaturaService.obtenerEstadisticasTemperatura(inicio, fin);
        return ResponseEntity.ok(estadisticas);
    }

    @GetMapping("/actual")
    public ResponseEntity<TemperaturaEntity> getTemperaturaActual() {
        TemperaturaEntity actual = temperaturaService.obtenerTemperaturaActual();
        return actual != null ? ResponseEntity.ok(actual) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<TemperaturaEntity> registrarTemperatura(@RequestBody TemperaturaEntity temperatura) {
        try {
            TemperaturaEntity nuevaTemperatura = temperaturaService.registrarTemperatura(temperatura);
            return ResponseEntity.ok(nuevaTemperatura);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
