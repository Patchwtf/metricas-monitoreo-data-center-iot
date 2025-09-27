package com.metricas_monitoreo_data_center_iot.com.demo.service.dto;

import com.metricas_monitoreo_data_center_iot.com.demo.enums.EstatusUsuario;
import com.metricas_monitoreo_data_center_iot.com.demo.persistence.entity.UsuarioEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class UsuarioDTO {
    private String idUsuario;
    private String nombre;
    private String apellido;
    private String correo;
    private EstatusUsuario estatus;
    private Integer numIntentos;
    private RolesDTO rol;
    private List<MaquinaDTO> maquinas;

    public UsuarioDTO() {}

    public UsuarioDTO(UsuarioEntity usuario) {
        this(usuario, true, true, true);
    }

    public UsuarioDTO(UsuarioEntity usuario, boolean incluirRol, boolean incluirMaquinas, boolean incluirIntentos) {
        this.idUsuario = usuario.getIdUsuario();
        this.nombre = usuario.getNombre();
        this.apellido = usuario.getApellido();
        this.correo = usuario.getCorreo();
        this.estatus = usuario.getEstatus();

        if (incluirIntentos && usuario.getNumIntentos() != null) {
            this.numIntentos = usuario.getNumIntentos();
        }

        if (incluirRol && usuario.getRol() != null) {
            this.rol = RolesDTO.crearSimple(usuario.getRol());
        }

        if (incluirMaquinas && usuario.getMaquinas() != null && !usuario.getMaquinas().isEmpty()) {
            this.maquinas = usuario.getMaquinas().stream()
                    .map(MaquinaDTO::new)
                    .collect(Collectors.toList());
        }
    }

    public static UsuarioDTO crearSimple(UsuarioEntity usuario) {
        return new UsuarioDTO(usuario, false, false, false);
    }

    public static UsuarioDTO crearConRol(UsuarioEntity usuario) {
        return new UsuarioDTO(usuario, true, false, false);
    }
}