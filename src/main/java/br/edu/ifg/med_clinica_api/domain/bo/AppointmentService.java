package br.edu.ifg.med_clinica_api.domain.bo;

import br.edu.ifg.med_clinica_api.domain.dto.appointment.AppointmentHistoryDTO;
import br.edu.ifg.med_clinica_api.domain.dto.pages.PageResponseDTO;
import br.edu.ifg.med_clinica_api.domain.entity.Appointment;
import br.edu.ifg.med_clinica_api.domain.enums.AppointmentStatus;
import br.edu.ifg.med_clinica_api.domain.dao.AppointmentRepository;
import br.edu.ifg.med_clinica_api.domain.dto.appointment.AppointmentDetailDTO;
import br.edu.ifg.med_clinica_api.domain.dto.appointment.AppointmentRegisterDTO;
import br.edu.ifg.med_clinica_api.domain.dto.appointment.AppointmentUpdateDTO;
import br.edu.ifg.med_clinica_api.domain.dao.DoctorRepository;
import br.edu.ifg.med_clinica_api.domain.dao.PatientRepository;
import br.edu.ifg.med_clinica_api.infra.audit.AuditAction;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    @Transactional
    @AuditAction("CRIAR_CONSULTA")
    public AppointmentDetailDTO scheduleAppointment(
            AppointmentRegisterDTO data
    ) {
        validateScheduleStatus(data.status());

        var patient = patientRepository.findById(data.patientId())
                .orElseThrow(() -> new EntityNotFoundException("Paciente não encontrado!"));

        var doctor = doctorRepository.findById(data.doctorId())
                .orElseThrow(() -> new EntityNotFoundException("Médico não encontrado!"));

        validatePatientIsActive(patient.getActive());
        validateDoctorIsActive(doctor.getActive());
        validateDoctorAvailability(doctor.getId(), data.scheduleAt(), data.durationInMinutes(), null);

        Appointment appointment = new Appointment(patient, doctor, data);

        appointmentRepository.save(appointment);

        return new AppointmentDetailDTO(appointment);
    }

    @Transactional()
    public PageResponseDTO<AppointmentHistoryDTO> findPatientHistory(
            String authenticatedUserEmail,
            AppointmentStatus status,
            String search,
            Pageable pageable
    ) {
        var patient = patientRepository.findByUserEmail(authenticatedUserEmail)
                .orElseThrow(() -> new EntityNotFoundException("Paciente não encontrado."));

        var normalizedSearch = normalizeSearch(search);

        Page<AppointmentHistoryDTO> appointments;

        if (normalizedSearch == null) {
            appointments = appointmentRepository
                    .findPatientHistoryWithoutSearch(
                            patient.getId(),
                            status,
                            pageable
                    )
                    .map(AppointmentHistoryDTO::new);
        } else {
            appointments = appointmentRepository
                    .findPatientHistoryWithSearch(
                            patient.getId(),
                            status,
                            normalizedSearch,
                            pageable
                    )
                    .map(AppointmentHistoryDTO::new);
        }

        return new PageResponseDTO<>(appointments);
    }

    private String normalizeSearch(String search) {

        if (search == null || search.isBlank()) {
            return null;
        }

        return "%" + escapeLike(search.trim().toLowerCase()) + "%";
    }

    private String escapeLike(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }

    public Page<AppointmentDetailDTO> listAllAppointmentsByStatus(AppointmentStatus status, Pageable pagination) {

        if (status == null )
            throw new IllegalArgumentException("O status da consulta é obrigatório.");

        return appointmentRepository
                .findByStatus(status, pagination)
                .map(AppointmentDetailDTO::new);
    }

    @Transactional
    @AuditAction("ATUALIZAR_CONSULTA")
    public AppointmentDetailDTO updateAppointment(UUID id, AppointmentUpdateDTO data) {
        var appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Consulta nÃ£o encontrada."));

        validateDoctorAvailability(
                appointment.getDoctor().getId(),
                data.scheduleAt(),
                appointment.getDurationInMinutes(),
                appointment.getId()
        );

        appointment.updateData(data);

        return new AppointmentDetailDTO(appointment);
    }

    @Transactional
    @AuditAction("CANCELAR_CONSULTA")
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
        var appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Consulta nÃ£o encontrada."));
        return new AppointmentDetailDTO(appointment);
    }

    private void validateScheduleStatus(AppointmentStatus status) {
        if (status != AppointmentStatus.SCHEDULED) {
            throw new IllegalArgumentException("Novas consultas devem ser criadas com status SCHEDULED.");
        }
    }

    private void validatePatientIsActive(Boolean active) {
        if (!Boolean.TRUE.equals(active)) {
            throw new IllegalStateException("Paciente inativo nao pode agendar consulta.");
        }
    }

    private void validateDoctorIsActive(Boolean active) {
        if (!Boolean.TRUE.equals(active)) {
            throw new IllegalStateException("Medico inativo nao pode receber agendamento.");
        }
    }

    private void validateDoctorAvailability(
            UUID doctorId,
            LocalDateTime scheduleAt,
            Integer durationInMinutes,
            UUID ignoredAppointmentId
    ) {
        var requestedEnd = scheduleAt.plusMinutes(durationInMinutes);
        var candidateAppointments = appointmentRepository
                .findByDoctor_IdAndStatusAndScheduleAtBefore(
                        doctorId,
                        AppointmentStatus.SCHEDULED,
                        requestedEnd
                );

        var hasConflict = candidateAppointments.stream()
                .filter(appointment -> ignoredAppointmentId == null || !appointment.getId().equals(ignoredAppointmentId))
                .anyMatch(appointment -> overlaps(
                        scheduleAt,
                        requestedEnd,
                        appointment.getScheduleAt(),
                        appointment.getScheduleAt().plusMinutes(appointment.getDurationInMinutes())
                ));

        if (hasConflict) {
            throw new IllegalStateException("O medico nao esta disponivel neste horario.");
        }
    }

    private boolean overlaps(
            LocalDateTime requestedStart,
            LocalDateTime requestedEnd,
            LocalDateTime existingStart,
            LocalDateTime existingEnd
    ) {
        return requestedStart.isBefore(existingEnd) && requestedEnd.isAfter(existingStart);
    }
}
