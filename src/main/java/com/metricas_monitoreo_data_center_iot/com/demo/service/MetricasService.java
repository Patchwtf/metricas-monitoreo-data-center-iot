package com.metricas_monitoreo_data_center_iot.com.demo.service;

import com.metricas_monitoreo_data_center_iot.com.demo.persistence.entity.MaquinaEntity;
import com.metricas_monitoreo_data_center_iot.com.demo.persistence.entity.MetricasEntity;
import com.metricas_monitoreo_data_center_iot.com.demo.persistence.repository.MaquinaRepository;
import com.metricas_monitoreo_data_center_iot.com.demo.persistence.repository.MetricasRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class MetricasService {

    @Autowired
    private MetricasRepository metricasRepository;

    @Autowired
    private MaquinaRepository maquinaRepository;

    public MetricasEntity save(MetricasEntity metrica) {
        if (metrica.getTimestamp() == null) {
            metrica.setTimestamp(LocalDateTime.now());
        }
        return metricasRepository.save(metrica);
    }

    public Optional<MetricasEntity> findById(Integer id) {
        return metricasRepository.findById(id);
    }

    public List<MetricasEntity> findAll() {
        return metricasRepository.findAll();
    }

    public List<MetricasEntity> obtenerHistoricoCompleto(MaquinaEntity maquina) {
        return metricasRepository.findByMaquinaOrderByTimestampAsc(maquina);
    }

    public List<MetricasEntity> obtenerMetricasPorRango(MaquinaEntity maquina, LocalDate inicio, LocalDate fin) {
        return metricasRepository.findByMaquinaAndTimestampBetweenOrderByTimestampAsc(maquina, inicio, fin);
    }

    public List<MetricasEntity> obtenerUltimas100Metricas(MaquinaEntity maquina) {
        return metricasRepository.findTop100ByMaquinaOrderByTimestampDesc(maquina);
    }

    public Double obtenerMaximaRam(MaquinaEntity maquina) {
        MetricasEntity max = metricasRepository.findTopByMaquinaOrderByRamDesc(maquina);
        return max != null ? max.getRam().doubleValue() : 0.0;
    }

    public Double obtenerMaximaTemperatura(MaquinaEntity maquina) {
        MetricasEntity max = metricasRepository.findTopByMaquinaOrderByTemperaturaDesc(maquina);
        return max != null ? max.getTemperatura().doubleValue() : 0.0;
    }

    public Double obtenerMaximoProcesador(MaquinaEntity maquina) {
        MetricasEntity max = metricasRepository.findTopByMaquinaOrderByProcesadorDesc(maquina);
        return max != null ? max.getProcesador().doubleValue() : 0.0;
    }

    public Double obtenerMaximoEspacioDisco(MaquinaEntity maquina) {
        MetricasEntity max = metricasRepository.findTopByMaquinaOrderByEspacioDiscoDesc(maquina);
        return max != null ? max.getProcesador().doubleValue() : 0.0;
    }

    public Map<String, Double> obtenerPromediosUltimas24Horas(MaquinaEntity maquina) {
        LocalDateTime hace24Horas = LocalDateTime.now().minusHours(24);

        Map<String, Double> promedios = new HashMap<>();
        promedios.put("ram", metricasRepository.findPromedioRamUltimas24Horas(maquina, hace24Horas));
        promedios.put("procesador", metricasRepository.findPromedioProcesadorUltimas24Horas(maquina, hace24Horas));
        promedios.put("espacioDisco", metricasRepository.findPromedioEspacioDiscoUltimas24Horas(maquina, hace24Horas));
        promedios.put("temperatura", metricasRepository.findPromedioTemperaturaUltimas24Horas(maquina, hace24Horas));

        return promedios;
    }

    public Map<String, Object> obtenerDashboardMaquina(Integer maquinaId) {
        Optional<MaquinaEntity> maquinaOpt = maquinaRepository.findById(maquinaId);
        if (maquinaOpt.isEmpty()) {
            throw new RuntimeException("Máquina no encontrada");
        }

        MaquinaEntity maquina = maquinaOpt.get();
        Map<String, Object> dashboard = new HashMap<>();

        List<MetricasEntity> ultimas = obtenerUltimas100Metricas(maquina);
        if (!ultimas.isEmpty()) {
            dashboard.put("ultimaMetrica", ultimas.get(0));
        }

        dashboard.put("promedios24h", obtenerPromediosUltimas24Horas(maquina));

        dashboard.put("maximaRam", obtenerMaximaRam(maquina));
        dashboard.put("maximaTemperatura", obtenerMaximaTemperatura(maquina));
        dashboard.put("maximoProcesador", obtenerMaximoProcesador(maquina));

        dashboard.put("totalMetricas", ultimas.size());

        return dashboard;
    }
}