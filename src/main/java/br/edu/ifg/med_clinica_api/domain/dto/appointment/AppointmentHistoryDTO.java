package br.edu.ifg.med_clinica_api.domain.dto.appointment;

import br.edu.ifg.med_clinica_api.domain.dto.clinicunit.ClinicUnitSummaryDTO;
import br.edu.ifg.med_clinica_api.domain.dto.doctor.DoctorAppointmentDTO;
import br.edu.ifg.med_clinica_api.domain.entity.Appointment;
import br.edu.ifg.med_clinica_api.domain.enums.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentHistoryDTO(
        UUID id,
        LocalDateTime scheduleAt,
        AppointmentStatus status,
        Integer durationInMinutes,
        Boolean attendanceConfirmed,
        LocalDateTime attendanceConfirmedAt,
        DoctorAppointmentDTO doctor,
        ClinicUnitSummaryDTO clinicUnit,
        boolean canDelete
) {
    public AppointmentHistoryDTO(Appointment appointment) {
        this(
                appointment.getId(),
                appointment.getScheduleAt(),
                appointment.getStatus(),
                appointment.getDurationInMinutes(),
                Boolean.TRUE.equals(appointment.getAttendanceConfirmed()),
                appointment.getAttendanceConfirmedAt(),
                new DoctorAppointmentDTO(appointment.getDoctor()),
                appointment.getClinicUnit() != null ? new ClinicUnitSummaryDTO(appointment.getClinicUnit()) : null,
                appointment.getStatus() == AppointmentStatus.CANCELED
                        || appointment.getStatus() == AppointmentStatus.COMPLETED
        );
    }
}
