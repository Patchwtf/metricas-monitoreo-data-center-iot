package com.metricas_monitoreo_data_center_iot.com.demo.persistence.repository;

import com.metricas_monitoreo_data_center_iot.com.demo.enums.EstatusTemperatura;
import com.metricas_monitoreo_data_center_iot.com.demo.persistence.entity.TemperaturaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TemperaturaRepository extends JpaRepository<TemperaturaEntity, Integer> {
    List<TemperaturaEntity> findByRegistroBetweenOrderByRegistroDesc(LocalDateTime inicio, LocalDateTime fin);
    List<TemperaturaEntity> findByEstatusOrderByRegistroDesc(EstatusTemperatura estatus);
    List<TemperaturaEntity> findTop100ByOrderByRegistroDesc();
    List<TemperaturaEntity> findByTemperaturaGreaterThan(Double temperaturaLimite);

    @Query("SELECT AVG(t.temperatura) FROM TemperaturaEntity t WHERE t.registro BETWEEN :inicio AND :fin")
    Double findPromedioTemperaturaPorRango(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("SELECT MAX(t.temperatura) FROM TemperaturaEntity t WHERE t.registro BETWEEN :inicio AND :fin")
    Double findMaximaTemperaturaPorRango(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
}
