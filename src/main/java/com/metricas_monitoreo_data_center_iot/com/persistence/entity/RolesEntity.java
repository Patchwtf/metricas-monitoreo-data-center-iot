package com.metricas_monitoreo_data_center_iot.com.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
public class RolesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol", nullable = false)
    private Integer idRol;

    @Column(name = "nombre_rol", nullable = false)
    private String nombreRol;

    private String horarios;

    @OneToMany(mappedBy = "rol")
    private List<UsuarioEntity> usuarios;

}
