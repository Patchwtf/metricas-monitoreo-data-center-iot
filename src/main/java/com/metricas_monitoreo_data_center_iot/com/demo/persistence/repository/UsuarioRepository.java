package com.metricas_monitoreo_data_center_iot.com.demo.persistence.repository;

import com.metricas_monitoreo_data_center_iot.com.demo.enums.EstatusUsuario;
import com.metricas_monitoreo_data_center_iot.com.demo.persistence.entity.RolesEntity;
import com.metricas_monitoreo_data_center_iot.com.demo.persistence.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, String> {

    List<UsuarioEntity> findByRol(RolesEntity rol);
    List<UsuarioEntity> findByRol_NombreRol(String nombreRol);
    Optional<UsuarioEntity> findByCorreo(String correo);
    boolean existsByCorreo(String correo);
    List<UsuarioEntity> findByEstatus(EstatusUsuario estatus);

}
