package com.metricas_monitoreo_data_center_iot.com.demo.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    private Double ram;
    private Double procesador;
    private Double espacioDisco;
    private Double temperatura;

    @Column(nullable = false)
    private LocalDateTime timestamp;

}
