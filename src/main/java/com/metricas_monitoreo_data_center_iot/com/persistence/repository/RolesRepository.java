package com.metricas_monitoreo_data_center_iot.com.persistence.repository;

import com.metricas_monitoreo_data_center_iot.com.persistence.entity.RolesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolesRepository extends JpaRepository<RolesEntity, Integer> {
    Optional<RolesEntity> findByNombreRol(String nombreRol);
    boolean existsByNombreRol(String nombreRol);
}
