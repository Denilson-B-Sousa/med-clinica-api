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
        DoctorAppointmentDTO doctor,
        ClinicUnitSummaryDTO clinicUnit
) {
    public AppointmentHistoryDTO(Appointment appointment) {
        this(
                appointment.getId(),
                appointment.getScheduleAt(),
                appointment.getStatus(),
                appointment.getDurationInMinutes(),
                new DoctorAppointmentDTO(appointment.getDoctor()),
                appointment.getClinicUnit() != null ? new ClinicUnitSummaryDTO(appointment.getClinicUnit()) : null
        );
    }
}
