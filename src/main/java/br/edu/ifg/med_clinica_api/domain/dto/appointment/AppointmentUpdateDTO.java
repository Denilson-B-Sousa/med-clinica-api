package br.edu.ifg.med_clinica_api.domain.dto.appointment;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AppointmentUpdateDTO(

        @NotNull(message = "A nova data e hora da consulta sao obrigatorias.")
        @Future(message = "A nova data e hora devem ser futuras.")
        LocalDateTime scheduleAt
) {
}
