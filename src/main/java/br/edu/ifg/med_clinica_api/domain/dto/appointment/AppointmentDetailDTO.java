package br.edu.ifg.med_clinica_api.domain.dto.appointment;

import br.edu.ifg.med_clinica_api.domain.entity.Appointment;
import br.edu.ifg.med_clinica_api.domain.enums.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentDetailDTO(
        UUID id,

        UUID patientId,

        UUID doctorId,

        String doctorName,

        String doctorSpeciality,

        LocalDateTime scheduledAt,

        AppointmentStatus status,

        Integer durationInMinutes
) {
    public AppointmentDetailDTO(Appointment appointment) {
        this(
                appointment.getId(),
                appointment.getPatient().getId(),
                appointment.getDoctor().getId(),
                appointment.getDoctor().getName(),
                appointment.getDoctor().getSpeciality().name(),
                appointment.getScheduleAt(),
                appointment.getStatus(),
                appointment.getDurationInMinutes()
        );
    }
}
