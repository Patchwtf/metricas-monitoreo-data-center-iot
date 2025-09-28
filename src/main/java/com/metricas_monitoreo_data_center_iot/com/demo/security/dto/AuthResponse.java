package com.metricas_monitoreo_data_center_iot.com.demo.security.dto;

import com.metricas_monitoreo_data_center_iot.com.demo.enums.EstatusUsuario;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor

public class AuthResponse {
    private String token;
    private String type = "Bearer";  // Tipo de token
    private String correo;
    private List<String> roles;
    private String mensaje;
    private EstatusUsuario estatus;
    private Date expiresAt;

    public AuthResponse(String token, String type,String correo, List<String> roles, EstatusUsuario estatus, Date expiresAt) {
        this.token = token;
        this.correo = correo;
        this.roles = roles;
        this.estatus = estatus;
        this.expiresAt = expiresAt;
        this.type = type;
    }
}
