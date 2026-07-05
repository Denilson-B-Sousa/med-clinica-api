package br.edu.ifg.med_clinica_api.domain.bo;

import br.edu.ifg.med_clinica_api.domain.dao.AppointmentRepository;
import br.edu.ifg.med_clinica_api.domain.dao.ClinicUnitRepository;
import br.edu.ifg.med_clinica_api.domain.dao.DoctorRepository;
import br.edu.ifg.med_clinica_api.domain.dao.PatientRepository;
import br.edu.ifg.med_clinica_api.domain.dto.appointment.AdminAppointmentDTO;
import br.edu.ifg.med_clinica_api.domain.dto.appointment.AppointmentDetailDTO;
import br.edu.ifg.med_clinica_api.domain.dto.appointment.AppointmentHistoryDTO;
import br.edu.ifg.med_clinica_api.domain.dto.appointment.AppointmentRegisterDTO;
import br.edu.ifg.med_clinica_api.domain.dto.appointment.AppointmentUpdateDTO;
import br.edu.ifg.med_clinica_api.domain.dto.pages.PageResponseDTO;
import br.edu.ifg.med_clinica_api.domain.dto.pages.SimplePageResponseDTO;
import br.edu.ifg.med_clinica_api.domain.entity.Appointment;
import br.edu.ifg.med_clinica_api.domain.entity.ClinicUnit;
import br.edu.ifg.med_clinica_api.domain.entity.Doctor;
import br.edu.ifg.med_clinica_api.domain.enums.AppointmentPeriod;
import br.edu.ifg.med_clinica_api.domain.enums.AppointmentStatus;
import br.edu.ifg.med_clinica_api.infra.audit.AuditAction;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.JoinType;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final ClinicUnitRepository clinicUnitRepository;
    private final PatientRepository patientRepository;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            DoctorRepository doctorRepository,
            ClinicUnitRepository clinicUnitRepository,
            PatientRepository patientRepository
    ) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.clinicUnitRepository = clinicUnitRepository;
        this.patientRepository = patientRepository;
    }

    @Transactional
    @AuditAction("CRIAR_CONSULTA")
    public AppointmentDetailDTO scheduleAppointment(AppointmentRegisterDTO data) {
        validateScheduleStatus(data.status());

        var patient = patientRepository.findById(data.patientId())
                .orElseThrow(() -> new EntityNotFoundException("Paciente nao encontrado."));

        var doctor = doctorRepository.findById(data.doctorId())
                .orElseThrow(() -> new EntityNotFoundException("Medico nao encontrado."));

        var clinicUnit = findClinicUnit(data.clinicUnitId());

        validatePatientIsActive(patient.getActive());
        validateDoctorIsActive(doctor.getActive());
        validateClinicUnitIsActive(clinicUnit.getActive());
        validateDoctorClinicUnit(doctor, clinicUnit);
        validateDoctorAvailability(doctor.getId(), data.scheduleAt(), data.durationInMinutes(), null);

        var appointment = new Appointment(patient, doctor, clinicUnit, data);
        appointmentRepository.save(appointment);

        return new AppointmentDetailDTO(appointment);
    }

    @Transactional
    public PageResponseDTO<AppointmentHistoryDTO> findPatientHistory(
            String authenticatedUserEmail,
            AppointmentStatus status,
            String search,
            Pageable pageable
    ) {
        var patient = patientRepository.findByUserEmail(authenticatedUserEmail)
                .orElseThrow(() -> new EntityNotFoundException("Paciente nao encontrado."));

        var normalizedSearch = normalizeSearch(search);
        var now = LocalDateTime.now(ZoneOffset.UTC);
        Page<AppointmentHistoryDTO> appointments;

        if (normalizedSearch == null) {
            appointments = appointmentRepository
                    .findPatientHistoryWithoutSearch(patient.getId(), status, now, pageable)
                    .map(AppointmentHistoryDTO::new);
        } else {
            appointments = appointmentRepository
                    .findPatientHistoryWithSearch(patient.getId(), status, normalizedSearch, now, pageable)
                    .map(AppointmentHistoryDTO::new);
        }

        return new PageResponseDTO<>(appointments);
    }

    public Page<AppointmentDetailDTO> listAllAppointmentsByStatus(
            AppointmentStatus status,
            Pageable pagination
    ) {
        if (status == null) {
            throw new IllegalArgumentException("O status da consulta e obrigatorio.");
        }

        var appointments = status == AppointmentStatus.SCHEDULED
                ? appointmentRepository.findByStatusAndScheduleAtAfter(
                        status,
                        LocalDateTime.now(ZoneOffset.UTC),
                        pagination
                )
                : appointmentRepository.findByStatus(status, pagination);

        return appointments
                .map(AppointmentDetailDTO::new);
    }

    public SimplePageResponseDTO<AdminAppointmentDTO> findAdminAppointments(
            UUID clinicUnitId,
            UUID doctorId,
            String patientName,
            AppointmentStatus status,
            LocalDate date,
            AppointmentPeriod period,
            Pageable pageable
    ) {
        var dateRange = resolveDateRange(date, period);
        var hourRange = resolveHourRange(period);
        var now = LocalDateTime.now(ZoneOffset.UTC);

        var appointments = appointmentRepository
                .findAll(
                        buildAdminAppointmentsSpecification(
                                clinicUnitId,
                                doctorId,
                                normalizeSearch(patientName),
                                status,
                                dateRange.startAt(),
                                dateRange.endAt(),
                                hourRange.startHour(),
                                hourRange.endHour(),
                                now
                        ),
                        pageable
                )
                .map(AdminAppointmentDTO::new);

        return new SimplePageResponseDTO<>(appointments);
    }

    private Specification<Appointment> buildAdminAppointmentsSpecification(
            UUID clinicUnitId,
            UUID doctorId,
            String patientName,
            AppointmentStatus status,
            LocalDateTime startAt,
            LocalDateTime endAt,
            Integer startHour,
            Integer endHour,
            LocalDateTime now
    ) {
        return (root, query, criteriaBuilder) -> {
            var predicates = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();

            if (clinicUnitId != null) {
                predicates.add(criteriaBuilder.equal(root.join("clinicUnit", JoinType.LEFT).get("id"), clinicUnitId));
            }

            if (doctorId != null) {
                predicates.add(criteriaBuilder.equal(root.join("doctor").get("id"), doctorId));
            }

            if (patientName != null) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.join("patient").get("name")),
                        patientName,
                        '\\'
                ));
            }

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));

                if (status == AppointmentStatus.SCHEDULED) {
                    predicates.add(criteriaBuilder.greaterThan(root.get("scheduleAt"), now));
                }
            }

            if (startAt != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("scheduleAt"), startAt));
            }

            if (endAt != null) {
                predicates.add(criteriaBuilder.lessThan(root.get("scheduleAt"), endAt));
            }

            if (startHour != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        criteriaBuilder.function("date_part", Double.class, criteriaBuilder.literal("hour"), root.get("scheduleAt")),
                        startHour.doubleValue()
                ));
            }

            if (endHour != null) {
                predicates.add(criteriaBuilder.lessThan(
                        criteriaBuilder.function("date_part", Double.class, criteriaBuilder.literal("hour"), root.get("scheduleAt")),
                        endHour.doubleValue()
                ));
            }

            return criteriaBuilder.and(predicates.toArray(jakarta.persistence.criteria.Predicate[]::new));
        };
    }

    @Transactional
    @AuditAction("ATUALIZAR_CONSULTA")
    public AppointmentDetailDTO updateAppointment(UUID id, AppointmentUpdateDTO data) {
        var appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Consulta nao encontrada."));

        var clinicUnit = data.clinicUnitId() == null
                ? appointment.getClinicUnit()
                : findClinicUnit(data.clinicUnitId());

        if (clinicUnit == null) {
            throw new IllegalArgumentException("A unidade clinica e obrigatoria para reagendar a consulta.");
        }

        validateClinicUnitIsActive(clinicUnit.getActive());
        validateDoctorClinicUnit(appointment.getDoctor(), clinicUnit);
        validateDoctorAvailability(
                appointment.getDoctor().getId(),
                data.scheduleAt(),
                appointment.getDurationInMinutes(),
                appointment.getId()
        );

        appointment.updateData(data, clinicUnit);

        return new AppointmentDetailDTO(appointment);
    }

    @Transactional
    @AuditAction("CANCELAR_CONSULTA")
    public void cancelAppointment(UUID id) {
        var appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Consulta nao encontrada."));

        appointment.cancel();
    }

    @Transactional
    @AuditAction("DELETAR_CONSULTA")
    public void deleteAppointmentPermanently(UUID id) {
        if (!appointmentRepository.existsById(id)) {
            throw new EntityNotFoundException("Consulta nao encontrada.");
        }

        appointmentRepository.deleteById(id);
    }

    @Transactional
    @AuditAction("DELETAR_CONSULTA_DO_HISTORICO")
    public void deletePatientHistoryAppointment(UUID id, String authenticatedUserEmail) {
        var appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Consulta nao encontrada."));

        if (!appointment.getPatient().getUser().getEmail().equals(authenticatedUserEmail)) {
            throw new EntityNotFoundException("Consulta nao encontrada.");
        }

        if (appointment.getStatus() != AppointmentStatus.CANCELED
                && appointment.getStatus() != AppointmentStatus.COMPLETED) {
            throw new IllegalStateException(
                    "Somente consultas canceladas ou concluidas podem ser excluidas do historico."
            );
        }

        appointmentRepository.delete(appointment);
    }

    public AppointmentDetailDTO getAppointmentById(UUID id) {
        var appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Consulta nao encontrada."));

        return new AppointmentDetailDTO(appointment);
    }

    @Transactional
    @AuditAction("CONFIRMAR_PRESENCA_CONSULTA")
    public AppointmentDetailDTO confirmAttendance(UUID id, String authenticatedUserEmail) {
        var appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Consulta nao encontrada."));

        if (!appointment.getPatient().getUser().getEmail().equals(authenticatedUserEmail)) {
            throw new EntityNotFoundException("Consulta nao encontrada.");
        }

        appointment.confirmAttendance();

        return new AppointmentDetailDTO(appointment);
    }

    private ClinicUnit findClinicUnit(UUID clinicUnitId) {
        return clinicUnitRepository.findById(clinicUnitId)
                .orElseThrow(() -> new EntityNotFoundException("Unidade clinica nao encontrada."));
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

    private DateRange resolveDateRange(LocalDate date, AppointmentPeriod period) {
        if (date == null) {
            return new DateRange(null, null);
        }

        var startOfDay = date.atStartOfDay();

        return switch (period == null ? AppointmentPeriod.DAY : period) {
            case DAY -> new DateRange(startOfDay, startOfDay.plusDays(1));
            case MORNING -> new DateRange(startOfDay, startOfDay.plusHours(12));
            case AFTERNOON -> new DateRange(startOfDay.plusHours(12), startOfDay.plusHours(18));
            case EVENING -> new DateRange(startOfDay.plusHours(18), startOfDay.plusDays(1));
        };
    }

    private HourRange resolveHourRange(AppointmentPeriod period) {
        return switch (period == null || period == AppointmentPeriod.DAY ? AppointmentPeriod.DAY : period) {
            case DAY -> new HourRange(null, null);
            case MORNING -> new HourRange(0, 12);
            case AFTERNOON -> new HourRange(12, 18);
            case EVENING -> new HourRange(18, 24);
        };
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

    private void validateClinicUnitIsActive(Boolean active) {
        if (!Boolean.TRUE.equals(active)) {
            throw new IllegalStateException("Unidade clinica inativa nao pode receber agendamento.");
        }
    }

    private void validateDoctorClinicUnit(Doctor doctor, ClinicUnit clinicUnit) {
        if (doctor.getClinicUnit() == null || !doctor.getClinicUnit().getId().equals(clinicUnit.getId())) {
            throw new IllegalStateException("O medico nao atende na unidade clinica selecionada.");
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
                .findByDoctor_IdAndStatusInAndScheduleAtBefore(
                        doctorId,
                        List.of(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED),
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

    private record DateRange(LocalDateTime startAt, LocalDateTime endAt) {
    }

    private record HourRange(Integer startHour, Integer endHour) {
    }
}
