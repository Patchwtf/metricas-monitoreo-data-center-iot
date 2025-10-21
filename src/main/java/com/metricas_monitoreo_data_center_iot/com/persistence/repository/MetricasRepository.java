package com.metricas_monitoreo_data_center_iot.com.persistence.repository;

import com.metricas_monitoreo_data_center_iot.com.persistence.entity.MaquinaEntity;
import com.metricas_monitoreo_data_center_iot.com.persistence.entity.MetricasEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MetricasRepository extends JpaRepository<MetricasEntity, Integer> {
    List<MetricasEntity> findByMaquinaOrderByTimestampAsc(MaquinaEntity maquina);
    MetricasEntity findTopByMaquinaOrderByRamDesc(MaquinaEntity maquina);
    MetricasEntity findTopByMaquinaOrderByProcesadorDesc(MaquinaEntity maquina);
    MetricasEntity findTopByMaquinaOrderByEspacioDiscoDesc(MaquinaEntity maquina);
    MetricasEntity findTopByMaquinaOrderByTemperaturaDesc(MaquinaEntity maquina);
    List<MetricasEntity> findByMaquinaAndTimestampBetweenOrderByTimestampAsc(
            MaquinaEntity maquina, LocalDateTime inicio, LocalDateTime fin);
    List<MetricasEntity> findTop100ByMaquinaOrderByTimestampDesc(MaquinaEntity maquina);
    List<MetricasEntity> findByTemperaturaGreaterThan(Double temperatura);
    Long countByTimestampAfter(LocalDateTime date);
    @Query("SELECT AVG(m.ram) FROM MetricasEntity m WHERE m.maquina = :maquina AND m.timestamp >= :desde")
    Double findPromedioRamUltimas24Horas(@Param("maquina") MaquinaEntity maquina, @Param("desde") LocalDateTime desde);

    @Query("SELECT AVG(m.procesador) FROM MetricasEntity m WHERE m.maquina = :maquina AND m.timestamp >= :desde")
    Double findPromedioProcesadorUltimas24Horas(@Param("maquina") MaquinaEntity maquina, @Param("desde") LocalDateTime desde);

    @Query("SELECT AVG(m.espacioDisco) FROM MetricasEntity m WHERE m.maquina = :maquina AND m.timestamp >= :desde")
    Double findPromedioEspacioDiscoUltimas24Horas(@Param("maquina") MaquinaEntity maquina, @Param("desde") LocalDateTime desde);

    @Query("SELECT AVG(m.temperatura) FROM MetricasEntity m WHERE m.maquina = :maquina AND m.timestamp >= :desde")
    Double findPromedioTemperaturaUltimas24Horas(@Param("maquina") MaquinaEntity maquina, @Param("desde") LocalDateTime desde);
}