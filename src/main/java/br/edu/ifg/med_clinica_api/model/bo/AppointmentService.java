package br.edu.ifg.med_clinica_api.model.bo;

import br.edu.ifg.med_clinica_api.model.entity.Appointment;
import br.edu.ifg.med_clinica_api.model.enums.AppointmentStatus;
import br.edu.ifg.med_clinica_api.model.dao.AppointmentRepository;
import br.edu.ifg.med_clinica_api.model.dto.appointment.AppointmentDetailDTO;
import br.edu.ifg.med_clinica_api.model.dto.appointment.AppointmentRegisterDTO;
import br.edu.ifg.med_clinica_api.model.dto.appointment.AppointmentUpdateDTO;
import br.edu.ifg.med_clinica_api.model.dao.DoctorRepository;
import br.edu.ifg.med_clinica_api.model.dao.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, DoctorRepository doctorRepository, PatientRepository patientRepository) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    public AppointmentDetailDTO scheduleAppointment(
            AppointmentRegisterDTO data
    ) {

        var patient = patientRepository.findById(data.patientId())
                .orElseThrow(() -> new EntityNotFoundException("Paciente não encontrado!"));

        var doctor = doctorRepository.findById(data.doctorId())
                .orElseThrow(() -> new EntityNotFoundException("Médico não encontrado!"));

        Appointment appointment = new Appointment(patient, doctor, data);

        appointmentRepository.save(appointment);

        return new AppointmentDetailDTO(appointment);
    }


    public Page<AppointmentDetailDTO> listAllAppointmentsByStatus(AppointmentStatus status, Pageable pagination) {

        if (status == null )
            throw new IllegalArgumentException("O status da consulta é obrigatório.");

        return appointmentRepository
                .findByStatus(status, pagination)
                .map(AppointmentDetailDTO::new);
    }

    @Transactional
    public AppointmentDetailDTO updateAppointment(UUID id, AppointmentUpdateDTO data) {
        var appointment = appointmentRepository.getReferenceById(id);
        appointment.updateData(data);

        return new AppointmentDetailDTO(appointment);
    }

    @Transactional
    public void cancelAppointment(UUID id) {
        var appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Consulta não encontrada."));

        if(appointment.getStatus() == AppointmentStatus.CANCELED)
            throw new IllegalArgumentException("A consulta já está cancelada.");


        if (appointment.getStatus() == AppointmentStatus.COMPLETED)
            throw new IllegalStateException("Não é possível cancelar uma consulta já concluída.");


        appointment.cancel();

    }

    public AppointmentDetailDTO getAppointmentById(UUID id) {
        var appointment = appointmentRepository.getReferenceById(id);
        return new AppointmentDetailDTO(appointment);
    }
}
