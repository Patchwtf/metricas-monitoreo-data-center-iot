package com.metricas_monitoreo_data_center_iot.com.persistence.entity;

import com.metricas_monitoreo_data_center_iot.com.enums.EstatusTemperatura;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
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

    @Column(precision = 5, scale = 2)
    private BigDecimal temperatura;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstatusTemperatura estatus;
}
