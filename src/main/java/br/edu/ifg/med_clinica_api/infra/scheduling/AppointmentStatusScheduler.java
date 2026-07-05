package br.edu.ifg.med_clinica_api.infra.scheduling;

import br.edu.ifg.med_clinica_api.domain.dao.AppointmentRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class AppointmentStatusScheduler {

    private final AppointmentRepository appointmentRepository;

    public AppointmentStatusScheduler(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Scheduled(fixedDelayString = "${appointments.status-update-interval-ms:60000}")
    @Transactional
    public void completeFinishedAppointments() {
        appointmentRepository.completeFinishedAppointments(LocalDateTime.now(ZoneOffset.UTC));
    }
}
