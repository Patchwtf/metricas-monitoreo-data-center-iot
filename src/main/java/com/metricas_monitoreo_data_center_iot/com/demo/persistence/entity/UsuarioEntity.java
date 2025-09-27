package com.metricas_monitoreo_data_center_iot.com.demo.persistence.entity;

import com.metricas_monitoreo_data_center_iot.com.demo.enums.EstatusTemperatura;
import com.metricas_monitoreo_data_center_iot.com.demo.enums.EstatusUsuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

@Entity
@Table(name = "usuario")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
public class UsuarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_usuario", nullable = false, unique = true)
    private String idUsuario;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(nullable = false)
    private String correo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstatusUsuario estatus;

    @Column(name = "numero_intentos")
    private Integer numIntentos;

    @Column(nullable = false)
    private String password;

    @ManyToOne
    @JoinColumn(name = "id_rol", nullable = false)
    private RolesEntity rol;

    @OneToMany(mappedBy = "responsable")
    private List<MaquinaEntity> maquinas;
}
