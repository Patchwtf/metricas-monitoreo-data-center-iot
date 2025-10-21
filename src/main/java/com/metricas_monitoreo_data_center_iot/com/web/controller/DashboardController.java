package com.metricas_monitoreo_data_center_iot.com.web.controller;

import com.metricas_monitoreo_data_center_iot.com.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN') or hasRole('USUARIO')")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/estado-general")
    public ResponseEntity<Map<String, Object>> getEstadoGeneral() {
        try {
            Map<String, Object> estadoGeneral = dashboardService.obtenerEstadoGeneral();
            return ResponseEntity.ok(estadoGeneral);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/alertas-activas")
    public ResponseEntity<List<Map<String, Object>>> getAlertasActivas() {
        try {
            Map<String, Object> estadoGeneral = dashboardService.obtenerEstadoGeneral();
            List<Map<String, Object>> alertas = (List<Map<String, Object>>) estadoGeneral.get("alertasActivas");
            return ResponseEntity.ok(alertas);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/metricas-tiempo-real")
    public ResponseEntity<Map<String, Object>> getMetricasTiempoReal() {
        try {
            Map<String, Object> estadoGeneral = dashboardService.obtenerEstadoGeneral();
            Map<String, Object> metricas = (Map<String, Object>) estadoGeneral.get("metricasTiempoReal");
            return ResponseEntity.ok(metricas);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/resumen")
    public ResponseEntity<Map<String, Object>> getResumen() {
        try {
            Map<String, Object> estadoGeneral = dashboardService.obtenerEstadoGeneral();

            Map<String, Object> resumen = new HashMap<>();
            resumen.put("totalMaquinas", estadoGeneral.get("totalMaquinas"));
            resumen.put("maquinasActivas", estadoGeneral.get("maquinasActivas"));
            resumen.put("totalAlertas", estadoGeneral.get("totalAlertas"));
            resumen.put("ultimaActualizacion", estadoGeneral.get("ultimaActualizacion"));

            Map<String, Object> metricas = (Map<String, Object>) estadoGeneral.get("metricasTiempoReal");
            if (metricas != null) {
                resumen.put("temperaturaPromedio", metricas.get("temperaturaPromedio"));
                resumen.put("cpuPromedio", metricas.get("cpuPromedio"));
            }

            return ResponseEntity.ok(resumen);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
