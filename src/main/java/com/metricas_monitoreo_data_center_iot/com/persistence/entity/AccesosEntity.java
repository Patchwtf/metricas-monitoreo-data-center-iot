package com.metricas_monitoreo_data_center_iot.com.persistence.entity;

import com.metricas_monitoreo_data_center_iot.com.enums.AccionesAccesos;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Entity
@Table(name = "accesos")
@Getter
@Setter
@NoArgsConstructor
public class AccesosEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_registro", nullable = false)
    private Long idRegistro;

    private LocalDateTime time;
    @Column(name = "id_usuario", nullable = false)
    private Integer idUsuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccionesAccesos acciones;
}
