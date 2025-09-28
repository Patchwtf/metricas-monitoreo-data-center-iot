package com.metricas_monitoreo_data_center_iot.com.demo.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class RegisterRequest {
    @Email
    @NotBlank
    private String correo;

    @NotBlank
    @Size(min = 8)
    private String password;
    private String nombre;
    private String apellido;
    private Integer rol;
}
