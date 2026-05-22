package br.edu.ifg.med_clinica_api.model.dao;

import br.edu.ifg.med_clinica_api.model.entity.Appointment;
import br.edu.ifg.med_clinica_api.model.enums.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    Page<Appointment> findByStatus(AppointmentStatus status, Pageable pagination);
}
