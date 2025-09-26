package com.metricas_monitoreo_data_center_iot.com.demo.persistence.entity;

import com.metricas_monitoreo_data_center_iot.com.demo.enums.EstatusMaquina;
import com.metricas_monitoreo_data_center_iot.com.demo.enums.EstatusTemperatura;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "temperatura")
@Getter
@Setter
@NoArgsConstructor
public class TemperaturaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_registro", nullable = false)
    private Integer idRegistro;

    @Column(nullable = false)
    private LocalDateTime registro;

    @Column(nullable = false)
    private Double temperatura;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstatusTemperatura estatus;
}
