package com.metricas_monitoreo_data_center_iot.com.demo.service;

import com.metricas_monitoreo_data_center_iot.com.demo.enums.AccionesAccesos;
import com.metricas_monitoreo_data_center_iot.com.demo.enums.EstatusMaquina;
import com.metricas_monitoreo_data_center_iot.com.demo.enums.EstatusUsuario;
import com.metricas_monitoreo_data_center_iot.com.demo.persistence.entity.AccesosEntity;
import com.metricas_monitoreo_data_center_iot.com.demo.persistence.entity.MaquinaEntity;
import com.metricas_monitoreo_data_center_iot.com.demo.persistence.entity.MetricasEntity;
import com.metricas_monitoreo_data_center_iot.com.demo.persistence.entity.UsuarioEntity;
import com.metricas_monitoreo_data_center_iot.com.demo.persistence.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class DashboardService {

    @Autowired
    private MaquinaRepository maquinaRepository;

    @Autowired
    private MetricasRepository metricasRepository;

    @Autowired
    private TemperaturaRepository temperaturaRepository;

    @Autowired
    private AccesosRepository accesosRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Map<String, Object> obtenerEstadoGeneral() {
        Map<String, Object> dashboard = new HashMap<>();

        // 📊 ESTADÍSTICAS DE MÁQUINAS
        List<MaquinaEntity> todasMaquinas = maquinaRepository.findAll();
        long totalMaquinas = todasMaquinas.size();
        long maquinasActivas = todasMaquinas.stream()
                .filter(m -> m.getEstatus() == EstatusMaquina.ACTIVA)
                .count();
        long maquinasCriticas = todasMaquinas.stream()
                .filter(m -> m.getEstatus() == EstatusMaquina.MANTENIMIENTO)
                .count();

        dashboard.put("totalMaquinas", totalMaquinas);
        dashboard.put("maquinasActivas", maquinasActivas);
        dashboard.put("maquinasInactivas", totalMaquinas - maquinasActivas);
        dashboard.put("maquinasCriticas", maquinasCriticas);

        Map<String, Object> metricasTiempoReal = obtenerMetricasTiempoReal();
        dashboard.put("metricasTiempoReal", metricasTiempoReal);

        List<Map<String, Object>> alertasActivas = obtenerAlertasActivas();
        dashboard.put("alertasActivas", alertasActivas);
        dashboard.put("totalAlertas", alertasActivas.size());

        Map<String, Object> statsUsuarios = obtenerEstadisticasUsuarios();
        dashboard.put("estadisticasUsuarios", statsUsuarios);

        Map<String, Object> actividadReciente = obtenerActividadReciente();
        dashboard.put("actividadReciente", actividadReciente);

        dashboard.put("ultimaActualizacion", LocalDateTime.now());

        return dashboard;
    }

    private Map<String, Object> obtenerMetricasTiempoReal() {
        Map<String, Object> metricas = new HashMap<>();
        LocalDateTime hace15Minutos = LocalDateTime.now().minusMinutes(15);

        List<MaquinaEntity> maquinas = maquinaRepository.findAll();

        // ✅ INICIALIZAR como BigDecimal, no como double
        BigDecimal temperaturaPromedio = BigDecimal.ZERO;
        BigDecimal cpuPromedio = BigDecimal.ZERO;
        BigDecimal ramPromedio = BigDecimal.ZERO;
        int maquinasConMetricas = 0;

        for (MaquinaEntity maquina : maquinas) {
            List<MetricasEntity> metricasRecientes = metricasRepository
                    .findTop100ByMaquinaOrderByTimestampDesc(maquina);

            if (!metricasRecientes.isEmpty()) {
                MetricasEntity ultima = metricasRecientes.getFirst();

                // ✅ SUMAR correctamente con BigDecimal
                if (ultima.getTemperatura() != null) {
                    temperaturaPromedio = temperaturaPromedio.add(ultima.getTemperatura());
                }
                if (ultima.getProcesador() != null) {
                    cpuPromedio = cpuPromedio.add(ultima.getProcesador());
                }
                if (ultima.getRam() != null) {
                    ramPromedio = ramPromedio.add(ultima.getRam());
                }
                maquinasConMetricas++;
            }
        }

        if (maquinasConMetricas > 0) {
            // ✅ CALCULAR PROMEDIO con BigDecimal
            BigDecimal divisor = BigDecimal.valueOf(maquinasConMetricas);

            metricas.put("temperaturaPromedio",
                    temperaturaPromedio.divide(divisor, 2, RoundingMode.HALF_UP));
            metricas.put("cpuPromedio",
                    cpuPromedio.divide(divisor, 2, RoundingMode.HALF_UP));
            metricas.put("ramPromedio",
                    ramPromedio.divide(divisor, 2, RoundingMode.HALF_UP));
        } else {
            metricas.put("temperaturaPromedio", BigDecimal.ZERO);
            metricas.put("cpuPromedio", BigDecimal.ZERO);
            metricas.put("ramPromedio", BigDecimal.ZERO);
        }

        metricas.put("maquinasMonitoreadas", maquinasConMetricas);
        metricas.put("totalMaquinas", maquinas.size());

        return metricas;
    }

    private List<Map<String, Object>> obtenerAlertasActivas() {
        List<Map<String, Object>> alertas = new ArrayList<>();
        LocalDateTime hace1Hora = LocalDateTime.now().minusHours(1);

        List<MetricasEntity> metricasCalientes = metricasRepository
                .findByTemperaturaGreaterThan(60.0);

        for (MetricasEntity metrica : metricasCalientes) {
            if (metrica.getTimestamp().isAfter(hace1Hora)) {
                Map<String, Object> alerta = new HashMap<>();
                alerta.put("tipo", "TEMPERATURA_CRITICA");
                alerta.put("mensaje", "Temperatura crítica en " + metrica.getMaquina().getNombre());
                alerta.put("valor", metrica.getTemperatura());
                alerta.put("timestamp", metrica.getTimestamp());
                alerta.put("maquina", metrica.getMaquina().getMac());
                alerta.put("nivel", "ALTO");
                alertas.add(alerta);
            }
        }

        List<AccesosEntity> accesosSospechosos = accesosRepository
                .findByAccionesAndTimeAfter(AccionesAccesos.ACCESO_DENEGADO, hace1Hora);

        if (accesosSospechosos.size() > 5) {
            Map<String, Object> alerta = new HashMap<>();
            alerta.put("tipo", "ACCESOS_SOSPECHOSOS");
            alerta.put("mensaje", "Múltiples accesos denegados en la última hora");
            alerta.put("cantidad", accesosSospechosos.size());
            alerta.put("timestamp", LocalDateTime.now());
            alerta.put("nivel", "MEDIO");
            alertas.add(alerta);
        }

        List<MaquinaEntity> maquinasInactivas = maquinaRepository.findAll().stream()
                .filter(m -> m.getEstatus() == EstatusMaquina.INACTIVA)
                .toList();

        for (MaquinaEntity maquina : maquinasInactivas) {
            Map<String, Object> alerta = new HashMap<>();
            alerta.put("tipo", "MAQUINA_INACTIVA");
            alerta.put("mensaje", "Máquina inactiva: " + maquina.getNombre());
            alerta.put("maquina", maquina.getMac());
            alerta.put("timestamp", LocalDateTime.now());
            alerta.put("nivel", "BAJO");
            alertas.add(alerta);
        }

        return alertas;
    }

    private Map<String, Object> obtenerEstadisticasUsuarios() {
        Map<String, Object> stats = new HashMap<>();

        List<UsuarioEntity> todosUsuarios = usuarioRepository.findAll();
        stats.put("totalUsuarios", todosUsuarios.size());

        long usuariosActivos = todosUsuarios.stream()
                .filter(u -> u.getEstatus() == EstatusUsuario.ACTIVO)
                .count();
        stats.put("usuariosActivos", usuariosActivos);

        Map<String, Long> usuariosPorRol = todosUsuarios.stream()
                .collect(Collectors.groupingBy(
                        u -> u.getRol().getNombreRol(),
                        Collectors.counting()
                ));
        stats.put("usuariosPorRol", usuariosPorRol);

        return stats;
    }

    private Map<String, Object> obtenerActividadReciente() {
        Map<String, Object> actividad = new HashMap<>();
        LocalDateTime hace24Horas = LocalDateTime.now().minusHours(24);

        List<AccesosEntity> accesosRecientes = accesosRepository
                .findByTimeBetweenOrderByTimeDesc(hace24Horas, LocalDateTime.now());
        actividad.put("accesos24h", accesosRecientes.size());

        long metricasRegistradas = metricasRepository.countByTimestampAfter(hace24Horas);
        actividad.put("metricasRegistradas", metricasRegistradas);

        List<Map<String, Object>> ultimasAlertas = obtenerAlertasActivas().stream()
                .limit(5)
                .collect(Collectors.toList());
        actividad.put("ultimasAlertas", ultimasAlertas);

        return actividad;
    }
}
