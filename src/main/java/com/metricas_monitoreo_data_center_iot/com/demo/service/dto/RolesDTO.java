package com.metricas_monitoreo_data_center_iot.com.demo.service.dto;

import com.metricas_monitoreo_data_center_iot.com.demo.persistence.entity.RolesEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class RolesDTO {
    private Integer idRol;
    private String nombreRol;
    private String horarios;
    private List<UsuarioDTO> usuarios;

    public RolesDTO() {
    }

    public RolesDTO(RolesEntity rol) {
        this(rol, true);
    }

    public RolesDTO(RolesEntity rol, boolean incluirUsuarios) {
        this.idRol = rol.getIdRol();
        this.nombreRol = rol.getNombreRol();
        this.horarios = rol.getHorarios();

        if (!rol.getUsuarios().isEmpty() && incluirUsuarios) {
            this.usuarios = rol.getUsuarios().stream()
                    .map(UsuarioDTO::crearSimple)
                    .collect(Collectors.toList());
        }

    }
    public static RolesDTO crearSimple(RolesEntity rol){
        return new RolesDTO(rol, false);
    }
}