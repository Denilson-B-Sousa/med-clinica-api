package br.edu.ifg.med_clinica_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MedClinicaApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MedClinicaApiApplication.class, args);
	}

}
