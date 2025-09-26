package com.metricas_monitoreo_data_center_iot.com.demo.persistence.entity;

import com.metricas_monitoreo_data_center_iot.com.demo.enums.EstatusMaquina;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "maquina")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
public class MaquinaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_maquina", nullable = false)
    private Integer idMaquina;

    @Column(nullable = false)
    private String mac;

    @Column(nullable = false)
    private String ip;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstatusMaquina estatus;

    @ManyToOne
    @JoinColumn(name = "id_responsable", nullable = false)
    private UsuarioEntity responsable;
}
