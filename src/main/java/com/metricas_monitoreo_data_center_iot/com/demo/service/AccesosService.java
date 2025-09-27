package com.metricas_monitoreo_data_center_iot.com.demo.service;

import com.metricas_monitoreo_data_center_iot.com.demo.enums.AccionesAccesos;
import com.metricas_monitoreo_data_center_iot.com.demo.enums.EstatusUsuario;
import com.metricas_monitoreo_data_center_iot.com.demo.persistence.entity.AccesosEntity;
import com.metricas_monitoreo_data_center_iot.com.demo.persistence.entity.UsuarioEntity;
import com.metricas_monitoreo_data_center_iot.com.demo.persistence.repository.AccesosRepository;
import com.metricas_monitoreo_data_center_iot.com.demo.persistence.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class AccesosService {

    @Autowired
    private AccesosRepository accesosRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<AccesosEntity> obtenerTodosLosAccesos() {
        return accesosRepository.findAll();
    }

    public List<AccesosEntity> obtenerAccesosPorUsuario(Integer idUsuario) {
        return accesosRepository.findAllByIdUsuarioOrderByTime(idUsuario);
    }

    public List<AccesosEntity> obtenerAccesosPorRango(LocalDate inicio, LocalDate fin) {
        LocalDateTime inicioDateTime = inicio.atStartOfDay();
        LocalDateTime finDateTime = fin.atTime(23, 59, 59);
        return accesosRepository.findByTimeBetweenOrderByTimeDesc(inicioDateTime, finDateTime);
    }

    public List<AccesosEntity> obtenerAccesosPorAccion(AccionesAccesos accion) {
        return accesosRepository.findByAccionesOrderByTimeDesc(accion);
    }

    public Map<String, Object> obtenerEstadisticasAccesos() {
        Map<String, Object> stats = new HashMap<>();
        List<AccesosEntity> todosAccesos = accesosRepository.findAll();
        long totalAccesos = todosAccesos.size();
        long accesosExitosos = todosAccesos.stream()
                .filter(a -> a.getAcciones() == AccionesAccesos.ACCESO_CORRECTO)
                .count();
        long accesosDenegados = totalAccesos - accesosExitosos;
        stats.put("totalAccesos", totalAccesos);
        stats.put("accesosExitosos", accesosExitosos);
        stats.put("accesosDenegados", accesosDenegados);
        stats.put("tasaExito", totalAccesos > 0 ? (double) accesosExitosos / totalAccesos * 100 : 0);
        stats.put("ultimosAccesos", accesosRepository.findTop10ByOrderByTimeDesc());
        return stats;
    }

    public List<AccesosEntity> obtenerAccesosSospechosos() {
        LocalDateTime haceUnaHora = LocalDateTime.now().minusHours(1);
        return accesosRepository.findByAccionesAndTimeAfter(
                AccionesAccesos.ACCESO_DENEGADO, haceUnaHora);
    }

    public AccesosEntity registrarAccesoValidado(Integer idUsuario, AccionesAccesos accion) {
        UsuarioEntity usuario = usuarioRepository.findById(idUsuario.toString())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + idUsuario));

        if (usuario.getEstatus() == EstatusUsuario.BLOQUEADO) {
            AccesosEntity accesoDenegado = new AccesosEntity();
            accesoDenegado.setIdUsuario(idUsuario);
            accesoDenegado.setAcciones(AccionesAccesos.ACCESO_DENEGADO);
            accesoDenegado.setTime(LocalDateTime.now());
            return accesosRepository.save(accesoDenegado);
        }

        AccesosEntity acceso = new AccesosEntity();
        acceso.setIdUsuario(idUsuario);
        acceso.setAcciones(accion);
        acceso.setTime(LocalDateTime.now());
        return accesosRepository.save(acceso);
    }
}
