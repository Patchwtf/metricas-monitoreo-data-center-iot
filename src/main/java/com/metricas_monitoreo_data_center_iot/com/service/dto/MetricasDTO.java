package com.metricas_monitoreo_data_center_iot.com.service.dto;

import com.metricas_monitoreo_data_center_iot.com.persistence.entity.MetricasEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class MetricasDTO {
    private Long id;
    private MaquinaDTO maquina;
    private BigDecimal ram;
    private BigDecimal procesador;
    private BigDecimal espacioDisco;
    private BigDecimal temperatura;
    private LocalDateTime timestamp;

    public MetricasDTO() {
    }

    public MetricasDTO(MetricasEntity metricas) {
        this.id = metricas.getId();
        this.ram = metricas.getRam();
        this.procesador = metricas.getProcesador();
        this.espacioDisco = metricas.getEspacioDisco();
        this.temperatura = metricas.getTemperatura();
        this.timestamp = metricas.getTimestamp();

        if (metricas.getMaquina() != null) {
            this.maquina = MaquinaDTO.crearSimple(metricas.getMaquina());
        }
    }
}
