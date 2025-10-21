package com.metricas_monitoreo_data_center_iot.com.config;

import com.metricas_monitoreo_data_center_iot.com.persistence.entity.*;
import com.metricas_monitoreo_data_center_iot.com.persistence.repository.*;
import com.metricas_monitoreo_data_center_iot.com.enums.*;
import com.metricas_monitoreo_data_center_iot.com.enums.AccionesAccesos;
import com.metricas_monitoreo_data_center_iot.com.enums.EstatusMaquina;
import com.metricas_monitoreo_data_center_iot.com.enums.EstatusTemperatura;
import com.metricas_monitoreo_data_center_iot.com.enums.EstatusUsuario;
import com.metricas_monitoreo_data_center_iot.com.persistence.entity.*;
import com.metricas_monitoreo_data_center_iot.com.persistence.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MaquinaRepository maquinaRepository;

    @Autowired
    private MetricasRepository metricasRepository;

    @Autowired
    private TemperaturaRepository temperaturaRepository;

    @Autowired
    private AccesosRepository accesosRepository;

    @Override
    public void run(String... args) throws Exception {
        if (rolesRepository.count() == 0) {
            System.out.println("🔄 Cargando datos de prueba...");
            cargarDatosPrueba();
            System.out.println("✅ Datos de prueba cargados exitosamente");
        }
    }

    private Integer uuidToInteger(String uuid) {
        return Math.abs(uuid.hashCode()) % 1000000;
    }

    private void cargarDatosPrueba() {


        // 1. CARGAR ROLES
        RolesEntity rolAdmin = crearRolSiNoExiste("ADMIN", "Administrador del sistema", "24/7");
        RolesEntity rolServicio = crearRolSiNoExiste("SERVICIO", "Usuario para servicios automáticos", "24/7");
        RolesEntity rolUsuario = crearRolSiNoExiste("USUARIO", "Usuario regular de monitoreo", "Lunes-Viernes 8:00-18:00");

        // 2. CARGAR USUARIOS
        UsuarioEntity admin = crearUsuario("admin", "Sistema", "admin@monitoreo.com", "admin123", rolAdmin, EstatusUsuario.ACTIVO);
        UsuarioEntity servicio = crearUsuario("servicio", "Automático", "servicio@monitoreo.com", "servicio123", rolServicio, EstatusUsuario.ACTIVO);
        UsuarioEntity usuario1 = crearUsuario("Juan", "Pérez", "juan@monitoreo.com", "usuario123", rolUsuario, EstatusUsuario.ACTIVO);

        // 3. CARGAR MÁQUINAS
        MaquinaEntity servidorWeb = crearMaquina("SRV-WEB-01", "00:1B:44:11:3A:B7", "192.168.1.100", admin, EstatusMaquina.ACTIVA);
        MaquinaEntity servidorBD = crearMaquina("SRV-DB-01", "00:1B:44:11:3A:B8", "192.168.1.101", admin, EstatusMaquina.ACTIVA);
        MaquinaEntity servidorBackup = crearMaquina("SRV-BKP-01", "00:1B:44:11:3A:B9", "192.168.1.102", usuario1, EstatusMaquina.MANTENIMIENTO);

        // 4. CARGAR MÉTRICAS DE PRUEBA
        cargarMetricasPrueba(servidorWeb, "Servidor Web");
        cargarMetricasPrueba(servidorBD, "Servidor Base de Datos");

        // 5. CARGAR TEMPERATURAS AMBIENTE
        cargarTemperaturasPrueba();

        // 6. CARGAR ACCESOS DE PRUEBA
        cargarAccesosPrueba(admin, usuario1);
    }

    private RolesEntity crearRolSiNoExiste(String nombre, String descripcion, String horarios) {
        return rolesRepository.findByNombreRol(nombre)
                .orElseGet(() -> {
                    RolesEntity rol = new RolesEntity();
                    rol.setNombreRol(nombre);
                    // rol.setDescripcion(descripcion); // Si tienes este campo
                    rol.setHorarios(horarios);
                    return rolesRepository.save(rol);
                });
    }

    private UsuarioEntity crearUsuario(String nombre, String apellido, String correo, String password, RolesEntity rol, EstatusUsuario estatus) {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setCorreo(correo);
        usuario.setPassword(password); // En producción usar BCrypt
        usuario.setRol(rol);
        usuario.setEstatus(estatus);
        usuario.setNumIntentos(0);
        return usuarioRepository.save(usuario);
    }

    private MaquinaEntity crearMaquina(String nombre, String mac, String ip, UsuarioEntity responsable, EstatusMaquina estatus) {
        MaquinaEntity maquina = new MaquinaEntity();
        maquina.setNombre(nombre);
        maquina.setMac(mac);
        maquina.setIp(ip);
        maquina.setFechaRegistro(LocalDateTime.now().minusDays(30));
        maquina.setEstatus(estatus);
        maquina.setResponsable(responsable);
        return maquinaRepository.save(maquina);
    }

    private void cargarMetricasPrueba(MaquinaEntity maquina, String nombreServidor) {
        LocalDateTime ahora = LocalDateTime.now();

        System.out.println("📊 Cargando métricas para: " + nombreServidor);

        // Crear métricas de las últimas 48 horas (cada 30 minutos)
        for (int i = 96; i >= 0; i--) {
            LocalDateTime timestamp = ahora.minusMinutes(i * 30);

            MetricasEntity metrica = new MetricasEntity();
            metrica.setMaquina(maquina);
            metrica.setTimestamp(timestamp);

            // Valores realistas con algo de variación
            double variacion = Math.sin(i * 0.1) * 10; // Variación sinusoidal

            double tempValor = 45.0 + variacion + (Math.random() * 10);
            metrica.setTemperatura(BigDecimal.valueOf(tempValor));

            double cpuValor = 30.0 + variacion + (Math.random() * 40);
            metrica.setProcesador(BigDecimal.valueOf(cpuValor));

            double ramValor = 40.0 + variacion + (Math.random() * 30);
            metrica.setRam(BigDecimal.valueOf(ramValor));

            double discoValor = 70.0 + (Math.random() * 25);
            metrica.setEspacioDisco(BigDecimal.valueOf(discoValor));

            metricasRepository.save(metrica);

            if (i % 24 == 0) System.out.print("."); // Progress indicator
        }
        System.out.println(" ✅");
    }

    private void cargarTemperaturasPrueba() {
        System.out.println("🌡️ Cargando temperaturas ambiente...");

        LocalDateTime ahora = LocalDateTime.now();

        for (int i = 0; i < 50; i++) {
            TemperaturaEntity temp = new TemperaturaEntity();
            temp.setRegistro(ahora.minusMinutes(i * 30)); // Cada 30 minutos

            // Temperatura ambiente realista (20-28°C) con variación diurna
            double horaDelDia = (ahora.getHour() + (i * 0.5)) % 24;
            double variacionDiurna = Math.sin(horaDelDia * Math.PI / 12) * 4;
            temp.setTemperatura(new BigDecimal(24.0 + variacionDiurna + (Math.random() * 2)));

            // Asignar estatus automáticamente
            if (temp.getTemperatura().intValue() > 26) {
                temp.setEstatus(EstatusTemperatura.TEMPERATURA_ALTA);
            } else if (temp.getTemperatura().intValue() < 22) {
                temp.setEstatus(EstatusTemperatura.TEMPERATURA_BAJA);
            } else {
                temp.setEstatus(EstatusTemperatura.TEMPERATURA_NORMAL);
            }

            temperaturaRepository.save(temp);
        }
        System.out.println("✅ Temperaturas ambiente cargadas");
    }

    private void cargarAccesosPrueba(UsuarioEntity admin, UsuarioEntity usuario) {
        System.out.println("🔐 Cargando accesos de prueba...");

        LocalDateTime ahora = LocalDateTime.now();

        // Accesos exitosos
        for (int i = 0; i < 20; i++) {
            AccesosEntity acceso = new AccesosEntity();
            acceso.setIdUsuario(uuidToInteger(admin.getIdUsuario()));
            acceso.setAcciones(AccionesAccesos.ACCESO_CORRECTO);
            acceso.setTime(ahora.minusHours(i * 2));
            accesosRepository.save(acceso);
        }

        // Algunos accesos denegados
        for (int i = 0; i < 5; i++) {
            AccesosEntity acceso = new AccesosEntity();
            acceso.setIdUsuario(Integer.parseInt(usuario.getIdUsuario().substring(0, 8)));
            acceso.setAcciones(AccionesAccesos.ACCESO_DENEGADO);
            acceso.setTime(ahora.minusMinutes(i * 30));
            accesosRepository.save(acceso);
        }

        System.out.println("✅ Accesos de prueba cargados");
    }
}