package br.edu.ifg.med_clinica_api.domain.appointment.dto;

import br.edu.ifg.med_clinica_api.domain.appointment.AppointmentStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentRegisterDTO(

        @NotNull(message = "O paciente é obrigatório para o agendamento da consulta.")
        UUID patientId,

        @NotNull(message = "O médico é obrigatório para o agendamento da consulta.")
        UUID doctorId,

        @NotNull(message = "A data e hora da consulta são obrigatórias.")
        @Future(message = "A data e hora devem ser futuras.")
        LocalDateTime scheduleAt,

        @NotNull
        AppointmentStatus status,

        @NotNull
        Integer durationInMinutes
) {
}
