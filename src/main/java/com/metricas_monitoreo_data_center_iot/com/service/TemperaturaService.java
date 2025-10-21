package com.metricas_monitoreo_data_center_iot.com.service;

import com.metricas_monitoreo_data_center_iot.com.enums.EstatusTemperatura;
import com.metricas_monitoreo_data_center_iot.com.persistence.entity.TemperaturaEntity;
import com.metricas_monitoreo_data_center_iot.com.persistence.repository.TemperaturaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TemperaturaService {

    @Autowired
    private TemperaturaRepository temperaturaRepository;

    private static final Double TEMPERATURA_ALERTA = 40.0;
    private static final Double TEMPERATURA_CRITICA = 60.0;

    public TemperaturaEntity registrarTemperatura(TemperaturaEntity temperatura) {
        if (temperatura.getRegistro() == null) {
            temperatura.setRegistro(LocalDateTime.now());
        }

        temperatura.setEstatus(calcularEstatusTemperatura(temperatura.getTemperatura().doubleValue()));
        return temperaturaRepository.save(temperatura);
    }

    public List<TemperaturaEntity> obtenerTodasLasTemperaturas() {
        return temperaturaRepository.findAll();
    }

    public List<TemperaturaEntity> obtenerTemperaturasPorRango(LocalDateTime inicio, LocalDateTime fin) {
        return temperaturaRepository.findByRegistroBetweenOrderByRegistroDesc(inicio, fin);
    }

    public List<TemperaturaEntity> obtenerTemperaturasPorEstatus(EstatusTemperatura estatus) {
        return temperaturaRepository.findByEstatusOrderByRegistroDesc(estatus);
    }

    public List<TemperaturaEntity> obtenerUltimasTemperaturas() {
        return temperaturaRepository.findTop100ByOrderByRegistroDesc();
    }

    public List<TemperaturaEntity> obtenerAlertasTemperatura() {
        return temperaturaRepository.findByTemperaturaGreaterThan(TEMPERATURA_ALERTA);
    }

    public Map<String, Object> obtenerEstadisticasTemperatura(LocalDateTime inicio, LocalDateTime fin) {
        Map<String, Object> stats = new HashMap<>();

        Double promedio = temperaturaRepository.findPromedioTemperaturaPorRango(inicio, fin);
        Double maxima = temperaturaRepository.findMaximaTemperaturaPorRango(inicio, fin);

        List<TemperaturaEntity> temperaturas = temperaturaRepository.findByRegistroBetweenOrderByRegistroDesc(inicio, fin);

        long totalRegistros = temperaturas.size();
        long alertas = temperaturas.stream()
                .filter(t -> t.getTemperatura().doubleValue() > TEMPERATURA_ALERTA)
                .count();
        long criticas = temperaturas.stream()
                .filter(t -> t.getTemperatura().doubleValue() > TEMPERATURA_CRITICA)
                .count();

        stats.put("promedio", promedio != null ? promedio : 0.0);
        stats.put("maxima", maxima != null ? maxima : 0.0);
        stats.put("totalRegistros", totalRegistros);
        stats.put("alertas", alertas);
        stats.put("criticas", criticas);
        stats.put("tasaAlertas", totalRegistros > 0 ? (double) alertas / totalRegistros * 100 : 0);

        return stats;
    }

    public TemperaturaEntity obtenerTemperaturaActual() {
        List<TemperaturaEntity> ultimas = temperaturaRepository.findTop100ByOrderByRegistroDesc();
        return ultimas.isEmpty() ? null : ultimas.getFirst();
    }

    private EstatusTemperatura calcularEstatusTemperatura(Double temperatura) {
        if (temperatura == null) return EstatusTemperatura.TEMPERATURA_NORMAL;

        if (temperatura > TEMPERATURA_CRITICA) return EstatusTemperatura.TEMPERATURA_ALTA;
        if (temperatura > TEMPERATURA_ALERTA) return EstatusTemperatura.TEMPERATURA_MEDIANA_ALTA;
        if (temperatura < 10.0) return EstatusTemperatura.TEMPERATURA_BAJA;
        if (temperatura < 15.0) return EstatusTemperatura.TEMPERATURA_MEDIANA_BAJA;

        return EstatusTemperatura.TEMPERATURA_NORMAL;
    }
}
