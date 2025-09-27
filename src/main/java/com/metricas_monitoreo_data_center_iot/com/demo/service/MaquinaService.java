package com.metricas_monitoreo_data_center_iot.com.demo.service;

import com.metricas_monitoreo_data_center_iot.com.demo.enums.EstatusMaquina;
import com.metricas_monitoreo_data_center_iot.com.demo.persistence.entity.MaquinaEntity;
import com.metricas_monitoreo_data_center_iot.com.demo.persistence.entity.UsuarioEntity;
import com.metricas_monitoreo_data_center_iot.com.demo.persistence.repository.MaquinaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MaquinaService {
    @Autowired
    private MaquinaRepository maquinaRepository;

    public List<MaquinaEntity> findAll() {
        return maquinaRepository.findAll();
    }

    public Optional<MaquinaEntity> findById(Integer id) {
        return maquinaRepository.findById(id);
    }

    public MaquinaEntity save(MaquinaEntity maquina) {
        if (maquina.getFechaRegistro() == null) {
            maquina.setFechaRegistro(LocalDateTime.now());
        }
        if (maquina.getEstatus() == null) {
            maquina.setEstatus(EstatusMaquina.ACTIVA);
        }
        return maquinaRepository.save(maquina);
    }

    public void deleteById(Integer id) {
        maquinaRepository.deleteById(id);
    }

    public List<MaquinaEntity> findByResponsable(UsuarioEntity responsable) {
        return maquinaRepository.findAllByResponsable(responsable);
    }

    public List<MaquinaEntity> findByEstatus(EstatusMaquina estatus) {
        return maquinaRepository.findByEstatus(estatus);
    }

    public Optional<MaquinaEntity> findByMac(String mac) {
        return maquinaRepository.findByMac(mac);
    }

    public Optional<MaquinaEntity> findByIp(String ip) {
        return maquinaRepository.findByIp(ip);
    }

    public boolean existeMaquinaConMac(String mac) {
        return maquinaRepository.existsByMac(mac);
    }

    public boolean existeMaquinaConIp(String ip) {
        return maquinaRepository.existsByIp(ip);
    }
}
