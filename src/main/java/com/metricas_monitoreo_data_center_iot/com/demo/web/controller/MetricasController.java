package com.metricas_monitoreo_data_center_iot.com.demo.web.controller;

import com.metricas_monitoreo_data_center_iot.com.demo.persistence.entity.MaquinaEntity;
import com.metricas_monitoreo_data_center_iot.com.demo.persistence.entity.MetricasEntity;
import com.metricas_monitoreo_data_center_iot.com.demo.service.MaquinaService;
import com.metricas_monitoreo_data_center_iot.com.demo.service.MetricasService;
import com.metricas_monitoreo_data_center_iot.com.demo.service.dto.MetricasDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/metricas")
@CrossOrigin(origins = "*")
public class MetricasController {

    @Autowired
    private MetricasService metricasService;

    @Autowired
    private MaquinaService maquinaService;

    @PostMapping
    public ResponseEntity<MetricasDTO> crearMetrica(@RequestBody MetricasEntity metrica) {
        try {
            MetricasEntity nuevaMetrica = metricasService.save(metrica);
            return ResponseEntity.ok(new MetricasDTO(nuevaMetrica));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/maquina/{maquinaId}/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardMaquina(@PathVariable Integer maquinaId) {
        try {
            Map<String, Object> dashboard = metricasService.obtenerDashboardMaquina(maquinaId);
            return ResponseEntity.ok(dashboard);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/maquina/{maquinaId}/historico")
    public ResponseEntity<List<MetricasDTO>> getHistorico(
            @PathVariable Integer maquinaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {

        Optional<MaquinaEntity> maquina = maquinaService.findById(maquinaId);
        if (maquina.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<MetricasEntity> metricasEntities;

        if (inicio == null || fin == null) {
            metricasEntities = metricasService.obtenerUltimas100Metricas(maquina.get());
        } else {
            metricasEntities = metricasService.obtenerMetricasPorRango(maquina.get(), inicio, fin);
        }

        List<MetricasDTO> metricasDTO = metricasEntities.stream()
                .map(MetricasDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(metricasDTO);
    }

    @GetMapping("/maquina/{maquinaId}/maximos")
    public ResponseEntity<Map<String, Double>> getMaximosHistoricos(@PathVariable Integer maquinaId) {
        Optional<MaquinaEntity> maquina = maquinaService.findById(maquinaId);
        if (maquina.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Map<String, Double> maximos = new HashMap<>();
        maximos.put("ram", metricasService.obtenerMaximaRam(maquina.get()));
        maximos.put("espacio", metricasService.obtenerMaximoEspacioDisco(maquina.get()));
        maximos.put("temperatura", metricasService.obtenerMaximaTemperatura(maquina.get()));
        maximos.put("procesador", metricasService.obtenerMaximoProcesador(maquina.get()));

        return ResponseEntity.ok(maximos);
    }
}
