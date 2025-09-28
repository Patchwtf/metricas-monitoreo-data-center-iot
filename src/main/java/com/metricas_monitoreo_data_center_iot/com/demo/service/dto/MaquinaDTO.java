package com.metricas_monitoreo_data_center_iot.com.demo.service.dto;

import com.metricas_monitoreo_data_center_iot.com.demo.enums.EstatusMaquina;
import com.metricas_monitoreo_data_center_iot.com.demo.persistence.entity.MaquinaEntity;
import com.metricas_monitoreo_data_center_iot.com.demo.persistence.entity.UsuarioEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MaquinaDTO {
    private Integer idMaquina;
    private String nombre;
    private String mac;
    private String ip;
    private LocalDateTime fechaRegistro;
    private EstatusMaquina estatus;
    private UsuarioDTO responsable;

    public MaquinaDTO() {}

    public MaquinaDTO(MaquinaEntity maquina) {
        this(maquina, true, true);
    }

    public MaquinaDTO(MaquinaEntity maquina, boolean incluirIp, boolean incluirFechaReistro) {
        this.idMaquina = maquina.getIdMaquina();
        this.nombre = maquina.getNombre();
        this.mac = maquina.getMac();
        this.estatus = maquina.getEstatus();

        if (incluirFechaReistro && maquina.getFechaRegistro() != null) {
            this.fechaRegistro = maquina.getFechaRegistro();
        }
        if (incluirIp && maquina.getIp() != null) {
            this.ip = maquina.getIp();
        }

        if (maquina.getResponsable() != null) {
            this.responsable = UsuarioDTO.crearSimple(maquina.getResponsable());
        }
    }
    public static MaquinaDTO crearSimple(MaquinaEntity maquina) {
        return new MaquinaDTO(maquina, false, false);
    }
}