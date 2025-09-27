package com.metricas_monitoreo_data_center_iot.com.demo.persistence.repository;

import com.metricas_monitoreo_data_center_iot.com.demo.enums.EstatusMaquina;
import com.metricas_monitoreo_data_center_iot.com.demo.persistence.entity.MaquinaEntity;
import com.metricas_monitoreo_data_center_iot.com.demo.persistence.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaquinaRepository extends JpaRepository<MaquinaEntity, Integer> {
    List<MaquinaEntity> findAllByResponsable(UsuarioEntity usuario);
    Optional<MaquinaEntity> findByMac(String mac);
    Optional<MaquinaEntity> findByIp(String ip);
    List<MaquinaEntity> findByEstatus(EstatusMaquina estatus);
    boolean existsByMac(String mac);
    boolean existsByIp(String ip);
}
