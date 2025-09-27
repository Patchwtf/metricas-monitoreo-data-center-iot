package com.metricas_monitoreo_data_center_iot.com.demo.service.dto;

import com.metricas_monitoreo_data_center_iot.com.demo.enums.EstatusUsuario;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class ResultadoLogin {
    private boolean exitoso;
    private String mensaje;
    private EstatusUsuario estatusUsuario;
    private int intentosRestantes;

    public ResultadoLogin() {}

    public ResultadoLogin(boolean exitoso, String mensaje, EstatusUsuario estatusUsuario, int intentosRestantes) {
        this.exitoso = exitoso;
        this.mensaje = mensaje;
        this.estatusUsuario = estatusUsuario;
        this.intentosRestantes = intentosRestantes;
    }

    public static ResultadoLogin exitoso(String mensaje) {
        return new ResultadoLogin(true, mensaje, EstatusUsuario.ACTIVO, 0);
    }

    public static ResultadoLogin fallido(String mensaje, EstatusUsuario estatus, int intentosRestantes) {
        return new ResultadoLogin(false, mensaje, estatus, intentosRestantes);
    }
}

