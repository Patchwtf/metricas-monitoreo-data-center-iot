package com.metricas_monitoreo_data_center_iot.com.demo.service.dto;

import com.metricas_monitoreo_data_center_iot.com.demo.enums.AccionesAccesos;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.beans.ConstructorProperties;

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