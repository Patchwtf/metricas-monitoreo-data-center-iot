package com.metricas_monitoreo_data_center_iot.com.service.dto;

import com.metricas_monitoreo_data_center_iot.com.enums.AccionesAccesos;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistroAccesoRequest {
    private Integer idUsuario;
    private AccionesAccesos accion;

    public RegistroAccesoRequest(){}

    public RegistroAccesoRequest(Integer idUsuario, AccionesAccesos accion) {
        this.idUsuario = idUsuario;
        this.accion = accion;
    }
}