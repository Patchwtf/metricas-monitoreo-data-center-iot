package com.metricas_monitoreo_data_center_iot.com.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "metricas", indexes = {
        @Index(name = "idx_maquina_timestamp", columnList = "id_maquina,timestamp"),
        @Index(name = "idx_timestamp", columnList = "timestamp")
})
@Getter
@Setter
@NoArgsConstructor
public class MetricasEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_maquina", nullable = false)
    private MaquinaEntity maquina;

    private BigDecimal ram;
    private BigDecimal procesador;
    private BigDecimal espacioDisco;
    private BigDecimal temperatura;

    @Column(nullable = false)
    private LocalDateTime timestamp;

}
