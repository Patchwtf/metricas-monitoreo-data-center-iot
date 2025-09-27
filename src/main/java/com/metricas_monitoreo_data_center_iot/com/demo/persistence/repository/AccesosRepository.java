package com.metricas_monitoreo_data_center_iot.com.demo.persistence.repository;

import com.metricas_monitoreo_data_center_iot.com.demo.enums.AccionesAccesos;
import com.metricas_monitoreo_data_center_iot.com.demo.persistence.entity.AccesosEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AccesosRepository extends JpaRepository<AccesosEntity, Long> {
    List<AccesosEntity> findAllByIdUsuarioOrderByTime(Integer idUsuario);


    List<AccesosEntity> findByTimeBetweenOrderByTimeDesc(LocalDateTime inicio, LocalDateTime fin);
    List<AccesosEntity> findByAccionesOrderByTimeDesc(AccionesAccesos acciones);
    List<AccesosEntity> findTop10ByOrderByTimeDesc();
    List<AccesosEntity> findByAccionesAndTimeAfter(AccionesAccesos acciones, LocalDateTime time);

    @Query("SELECT COUNT(a) FROM AccesosEntity a WHERE a.idUsuario = :idUsuario")
    Long countByIdUsuario(@Param("idUsuario") Integer idUsuario);
}
