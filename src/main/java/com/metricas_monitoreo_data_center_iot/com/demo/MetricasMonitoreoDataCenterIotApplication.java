package com.metricas_monitoreo_data_center_iot.com.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
@EnableJpaAuditing
public class MetricasMonitoreoDataCenterIotApplication {
	public static void main(String[] args) {
		SpringApplication.run(MetricasMonitoreoDataCenterIotApplication.class, args);
	}
}
